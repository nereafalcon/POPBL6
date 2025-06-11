import pandas as pd
from geopy.geocoders import Nominatim
from geopy.extra.rate_limiter import RateLimiter
from datetime import date
import os

def enrich_with_district():
    # Obtener la fecha de hoy
    today = date.today().strftime("%Y-%m-%d")

    # Rutas de entrada y salida
    input_dir = "/opt/nifi/data"
    output_dir = "/opt/nifi/data/dataEnriched"
    filename = f"idealista_toda_españa_{today}.csv"

    input_csv = os.path.join(input_dir, filename)
    output_csv = os.path.join(output_dir, filename)

    # Crear el directorio de salida si no existe
    os.makedirs(output_dir, exist_ok=True)

    # Leer CSV
    df = pd.read_csv(input_csv)

    # Inicializar geolocalizador
    geolocator = Nominatim(user_agent="idealista_enricher")
    geocode = RateLimiter(geolocator.reverse, min_delay_seconds=1)

    districts = []

    for idx, row in df.iterrows():
        try:
            location = geocode((row['latitude'], row['longitude']), language='es')
            if location and 'address' in location.raw:
                address = location.raw['address']
                district = address.get('suburb') or address.get('neighbourhood') or address.get('district') or ""
            else:
                district = ""
        except Exception as e:
            print(f"⚠️ Error en fila {idx} ({row['latitude']}, {row['longitude']}): {e}")
            district = ""

        districts.append(district)

    # Añadir columna nueva
    df['district'] = districts

    # Guardar CSV enriquecido
    df.to_csv(output_csv, index=False)
    print(f"✅ Archivo enriquecido guardado en: {output_csv}")

if _name_ == "_main_":
    enrich_with_district()