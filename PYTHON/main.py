import base64
import requests as rq
import json
import pandas as pd
from datetime import date
from dotenv import load_dotenv
import os

# Carga variables del archivo .env
load_dotenv()

def get_oauth_token():
    print("🔐 Solicitando token de acceso...")
    api_key = os.getenv('API_KEY')
    secret = os.getenv('SECRET')
    if not api_key or not secret:
        print("❌ Error: No se encontraron las variables de entorno API_KEY o SECRET.")
        return None

    message = f"{api_key}:{secret}"
    auth = "Basic " + base64.b64encode(message.encode("ascii")).decode("ascii")

    headers = {
        "Authorization": auth,
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    }

    data = {
        "grant_type": "client_credentials",
        "scope": "read"
    }

    response = rq.post("https://api.idealista.com/oauth/token", headers=headers, data=data)

    if response.status_code != 200:
        print("❌ Error al obtener token:", response.text)
        return None

    print("🔑 Token obtenido correctamente.")
    return json.loads(response.text)['access_token']

# El resto queda igual
def define_search_params(pagination):
    return {
        'operation': 'sale',
        'propertyType': 'homes',
        'country': 'es',
        'center': '40.4637,-3.7492',  # Centro aproximado de España
        'distance': '5000',          # Radio de 50 km
        'numPage': pagination,
        'maxItems': 50,
        'language': 'es'
    }

def search_api(params):
    token = get_oauth_token()
    if not token:
        return {}

    headers = {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    url = "https://api.idealista.com/3.5/es/search"
    response = rq.post(url, headers=headers, data=params)

    print(f"📡 Código de respuesta: {response.status_code}")
    if response.status_code != 200:
        print("❌ Error en la búsqueda:", response.text)
        return {}

    return json.loads(response.text)

def results_to_df(results):
    if 'elementList' not in results:
        print("⚠️ No hay resultados.")
        return pd.DataFrame()
    return pd.DataFrame(results['elementList'])

def ejecutar_busqueda_amplia():
    print("🔍 Realizando búsqueda general en España...")
    params = define_search_params(1)
    results = search_api(params)

    if not results or 'elementList' not in results:
        print("⚠️ No se recibieron resultados.")
        return

    total_pages = results.get('totalPages', 1)
    print(f"📄 Total de páginas: {total_pages}")

    df_tot = results_to_df(results)

    for i in range(2, min(4, total_pages + 1)):  # Limita a 3 páginas (150 resultados máx)
        print(f"🔁 Cargando página {i}")
        params = define_search_params(i)
        results = search_api(params)
        df = results_to_df(results)
        df_tot = pd.concat([df_tot, df], ignore_index=True)

    print(f"📊 Total viviendas recogidas: {len(df_tot)}\n")

    if not df_tot.empty:
        print("🖼️ Primeras 5 viviendas:")
        print(df_tot[['price', 'size', 'propertyCode', 'address', 'url']].head())

        today = date.today()
        df_tot.to_csv(f"idealista_toda_españa_{today}.csv", index=False)
        print(f"✅ Resultados guardados en idealista_toda_españa_{today}.csv")

    print("🏁 Búsqueda finalizada.")

# Ejecutar
ejecutar_busqueda_amplia()
