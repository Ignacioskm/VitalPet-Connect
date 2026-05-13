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

Para probar el ciclo de vida completo del negocio en Postman o Swagger, ejecuta los siguientes pasos en orden:

### Fase 1: Infraestructura y Catálogos Base
1. **ms-branches:** Crea una Ciudad y luego una Sucursal en esa ciudad.
2. **ms-staff:** Crea las Especialidades médicas (ej. "Cirujano", "Medicina General").
3. **ms-pets:** Crea las Especies (ej. "Perro", "Gato").
4. **ms-appointments:** Crea los Servicios Médicos base con sus precios (ej. "Consulta General: $15.000").

### Fase 2: Actores Principales
5. **ms-users:** Registra dos usuarios (User 1 será un cliente regular, User 2 será para adopciones).
6. **ms-staff:** Contrata a un veterinario, asígnale el ID de la sucursal y la especialidad creados en la Fase 1.

### Fase 3: Registro de Pacientes
7. **ms-pets:** Registra una mascota asignándole el User 1 como dueño (ownerId).
8. **ms-pets:** Registra una mascota *sin dueño* (ownerId nulo) para las pruebas de adopción.

### Fase 4: Operaciones Médicas
9. **ms-appointments:** Agenda una cita enviando el ID de la mascota, veterinario, sucursal y servicio médico.
10. **ms-clinical-history:** Crea una ficha clínica para la mascota asociada a esa cita.

### Fase 5: Cierre y Facturación
11. **ms-appointments:** Cambia el estado de la cita a `COMPLETED`. Esto generará automáticamente la deuda en el microservicio de pagos.
12. **ms-payments:** Consulta los pagos pendientes del User 1.
13. **ms-payments:** Ejecuta el endpoint de pago (cambia a `PAID`). Esto disparará una alerta a través de `ms-notifications`.

### Fase Externa: Adopciones
14. **ms-adoptions:** Crea una solicitud de adopción para la mascota sin dueño asignándosela al User 2.
15. **ms-adoptions:** Aprueba la solicitud (`APPROVED`). El sistema actualizará automáticamente el dueño en `ms-pets`.

---

## 📚 6. Documentación API (Swagger)

Cada microservicio expone su propia documentación interactiva a través de Swagger. Una vez que los servicios estén corriendo, puedes acceder a la especificación de sus endpoints ingresando a:

* `http://localhost:[PUERTO_DEL_MICROSERVICIO]/swagger-ui.html`

