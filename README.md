# 🐾 VitalPet Connect

**VitalPet Connect** es un Sistema de Gestión Veterinaria robusto y escalable, construido bajo una arquitectura de microservicios. Está diseñado para centralizar y administrar todo el ciclo de vida de una clínica veterinaria: desde el registro de pacientes y agendamiento de citas, hasta la gestión clínica, facturación y adopciones.

---

## 🏗️ 1. Arquitectura del Sistema

El ecosistema de VitalPet Connect está diseñado bajo los patrones de **Spring Cloud** para garantizar alta disponibilidad y desacoplamiento:

*   **API Gateway (El Portero):** Es el único punto de entrada público al sistema. Enruta todas las peticiones del cliente (Frontend o Postman) hacia los microservicios internos.
*   **Eureka Server (El Directorio):** Actúa como Service Discovery. Ningún microservicio necesita conocer la IP o el puerto de los demás; todos se registran en Eureka y se encuentran a través de él.
*   **Seguridad y Autenticación:** Se utiliza **JWT (JSON Web Tokens)**. El Gateway y `ms-auth` validan las credenciales, emitiendo un token que los demás microservicios interceptan para asegurar que solo usuarios autorizados puedan ejecutar acciones.
*   **Comunicación Interna:** Para procesos síncronos donde un microservicio depende de otro (ej. validar si una mascota existe antes de crear una cita), se utiliza **OpenFeign**.

---

## 💻 2. Tecnologías Usadas

*   **Lenguaje:** Java 17+
*   **Framework Principal:** Spring Boot 3+
*   **Ecosistema Cloud:** Spring Cloud (Netflix Eureka, API Gateway, OpenFeign)
*   **Base de Datos:** MySQL
*   **Seguridad:** Spring Security + JWT
*   **Documentación:** Swagger / OpenAPI 3.0
*   **Despliegue/Infraestructura:** Docker & Docker Compose

---

## 📦 3. Estructura de Microservicios

El sistema está dividido en dominios de negocio específicos:

*   🔐 **ms-auth:** Emisión y validación de tokens de seguridad JWT.
*   👥 **ms-users:** Gestión de usuarios (clientes/dueños) y roles del sistema.
*   🏥 **ms-branches:** Administración de sucursales veterinarias y ciudades.
*   👨‍⚕️ **ms-staff:** Gestión del personal médico, especialidades y horarios.
*   🐕 **ms-pets:** Registro de pacientes (mascotas) y catálogo de especies.
*   📅 **ms-appointments:** Agendamiento de citas y catálogo de servicios médicos parametrizados.
*   📋 **ms-clinical-history:** Registro de diagnósticos, anamnesis y recetas médicas.
*   💳 **ms-payments:** Generación de deudas, procesamiento de pagos y reembolsos.
*   🔔 **ms-notifications:** Envío de alertas internas y comprobantes a los usuarios.
*   🏡 **ms-adoptions:** Gestión de mascotas sin hogar y flujo de adopción.

---

## 🚀 4. Instrucciones de Levantamiento

Para ejecutar el proyecto en un entorno local, sigue este orden estricto de inicialización para evitar errores de dependencias:

1.  **Base de Datos:** Asegúrate de tener MySQL corriendo en tu máquina (puerto 3306) o levanta el archivo `docker-compose.yml`. Las bases de datos (`vitalpet_users`, `vitalpet_pets`, etc.) se crearán automáticamente si configuraste la propiedad de conexión en los archivos `.yml`.
2.  **Infraestructura Base:** 
    *   Inicia `eureka-server` y espera a que levante completamente.
3.  **Seguridad y Enrutamiento:**
    *   Inicia `api-gateway`.
    *   Inicia `ms-auth`.
4.  **Microservicios de Negocio:**
    *   Levanta el resto de los microservicios en cualquier orden (`ms-users`, `ms-branches`, `ms-staff`, `ms-pets`, etc.).

---

## 🛤️ 5. Flujo de Pruebas (El "Camino Feliz")

Probar el funcionamiento correcto de todos los MS.
Dividimos el funcionamiento en 5 fases y una extra para las adopciones, para considerar terminado el proyecto tenemos que cumplir si o si con este flujo más las excepciones.

Fase 1: Infraestructura y Catálogos Base (Los que no dependen de nadie)

    Levanta la infraestructura: Encender eureka-server, esperar a que inicie, y luego levantar ms-auth.
    ms-branches: Verificar si se crearon las ciudades (City) con data.sql y luego crea una sucursal (Branch) en una ciudad.
    ms-staff: Verificar si se crearon las especialidades médicas (Specialty), por ejemplo, "Cirujano" o "Medicina General".
    ms-pets: Verificar si se crearon las espcies (Species) como "Perro" y "Gato".
    ms-appointments: Verificar si se crearon los servicios médicos (MedicalService) con sus respectivos precios.

Fase 2: Actores Principales (Dependen de la Fase 1)

    ms-users: Registrar al menos dos usuarios. Uno que será el "Cliente/Dueño" y otro para usar en las adopciones.
    ms-staff: Contratar al primer veterinario. Asignale el ID de la sucursal (Branch) y la especialidad (Specialty) que verificamos en la fase 1. Configurar el horario correspondiente (Schedule).

Fase 3: El Núcleo del Negocio

    ms-pets: Crear una mascota. Se le tendrá que pasar el ID de la especie (Fase 1) y el ID del usuario dueño (Fase 2).
    ms-pets (Adopción preparativo): Crear una mascota sin dueño (ownerId null) para probar el flujo de adopciones más adelante.

Fase 4: Operaciones y Transacciones

    ms-appointments: Crear una cita médica. El JSON deberá tener el ID de la mascota, el ID del veterinario, el ID de la sucursal y el ID del servicio médico. ¡Aquí Feign hará todo su trabajo de validación!
    ms-clinical-history: Con la mascota en la clínica, el veterinario le crea una ficha clínica (ClinicalRecord) detallando el diagnóstico.

Fase 5: Cierre y Facturación

    ms-appointments: Cambia el estado de la cita a COMPLETED. Aquí la magia ocurre: tu código disparará el evento interno para crear la deuda.
    ms-payments: Revisa los pagos pendientes del usuario (GET /api/payments/user/{userId}/pending). Deberías ver la deuda que acaba de generar la cita.
    ms-payments: Ejecuta el método pay (pagar). Esto debe cambiar el estado a PAID y, por debajo, llamar a ms-notifications.
    ms-notifications: Revisa las notificaciones del usuario para confirmar que llegó el comprobante de pago.

Fase Externa: Adopciones

    ms-adoptions: Pide la lista de mascotas disponibles (/available-pets).
    ms-adoptions: Crea una solicitud de adopción para la mascota sin dueño que creaste en el paso 9, asignándosela al usuario número 2.
    ms-adoptions: Aprueba la adopción (estado APPROVED). Esto llamará a ms-pets y le pondrá el dueño a la mascota.

## 📚 6. Documentación API (Swagger)

Cada microservicio expone su propia documentación interactiva a través de Swagger. Una vez que los servicios estén corriendo, puedes acceder a la especificación de sus endpoints ingresando a:

* `http://localhost:[PUERTO_DEL_MICROSERVICIO]/swagger-ui.html`

**Asignatura:** Desarrollo Fullstack I  
**Integrantes:** Ignacio Mellado · Martina Molina · Vicente Arredondo  
