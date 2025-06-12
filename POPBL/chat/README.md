# POPBL6 - TALDE 8
# MUEF --> Mondragon Unibertsitateko Etxebizitza Filtratzailea (Java, RMI + RabbitMQ)

Este proyecto es una aplicación de **chat inmobiliario distribuido**, donde compradores y vendedores pueden interactuar entre sí, los vendedores pueden gestionar casas y cualquier usuario puede usar un chatbot de definiciones.  
Utiliza **Java RMI** para la lógica principal (register, login, crearCasa, etc.) y **RabbitMQ** para la mensajería instantánea y el bot.

---

## Requisitos previos

- Java 21 o superior
- RabbitMQ instalado y corriendo en una máquina accesible por todos los clientes y el servidor
- Acceso a la red local para todos los nodos (clientes y servidor)

---

## 1. Configuración de RabbitMQ

1. Instala RabbitMQ en una máquina de la red (puede ser el mismo servidor RMI o distinta).
2. Crea un usuario y darle permisos de lectura, escritura y configuración para el proyecto (en la web o por comandos):
   rabbitmqctl add_user user password
   Ejemplo: add_user popbl6 Mondragon.2022
   rabbitmqctl set_permissions -p / user ".*" ".*" ".*"
   Ejemplo: set_permissions -p / popbl6 ".*" ".*" ".*"

3. Asegúrate de que el puerto **5672** esté abierto para conexiones remotas.

---

## 2. Configuración del código

### Cambia las IPs y credenciales:

- **En `ClienteMain.java`**  
  Cambia la IP del servidor RMI:
  private static final String HOST = "IP_DEL_SERVIDOR_RMI";


- **En `RabbitMQUtil.java`**  
  Cambia los datos de conexión a RabbitMQ:
  private static final String HOST = "IP_DEL_SERVIDOR_RABBITMQ";
  private static final String USERNAME = "popbl6";
  private static final String PASSWORD = "Mondragon.2022";

Se puede usar la misma IP si RMI y RabbitMQ están en la misma máquina.

---

## 3. Compilación

Desde la raíz del proyecto, ejecuta el siguiente comando para compilarlo:
mvn clean package

---

## 4. Ejecución

### 1. Inicia el servidor RMI

Correr el java ServidorMain.java para inicializar el servidor.

### 2. Inicia el bot de definiciones

Después, hay que iniciar el Bot, corriendo el java BotMain.java.
Puede estar en la misma u otra máquina (pero con acceso a RabbitMQ):

### 3. Inicia los clientes

En cada ordenador cliente (con acceso a la red) se debe correr el ClienteMain.java.
Es importante que todos los clientes tengan acceso a la IP del servidor y a la de RabbitMQ, por lo tanto cuidado con las IPs configuradas en el código. 

---

## 5. Utilización

Los usuarios que interactúan son solamente los clientes. Estas son las funcionalidades que pueden hacer desde el programa:

- Regístrarse o iniciar sesión con un usuario y rol (comprador o vendedor)
- **Compradores**:
  - Buscar casas
  - Añadir favoritas
  - Iniciar chats con vendedores
  - Usar el chatbot de definiciones
- **Vendedores**:
  - Crear casas
  - Chatear con compradores
  - Usar el chatbot de definiciones

---

## 6. Notas importantes

Si se modifican las IPs o credenciales, se debe **recompilar** el proyecto.
- Si tienes problemas de conexión:
  - Comprueba que RabbitMQ esté **corriendo**
  - Revisa **firewalls** y puertos (5672 abierto)
  - Verifica que estás en la **misma red** local