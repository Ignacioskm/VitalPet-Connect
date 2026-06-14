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

## 🛤️ 5. Pruebas y Demostración.

Para ver el flujo completo de cómo interactúan los microservicios entre sí, puedes revisar nuestro documento detallado.

**[Ver documento de Flujo de Pruebas (PDF)](./docs/Funcionamiento_correcto_de_todos_los_MS..pdf)**

## 📚 6. Documentación API (Swagger)

Cada microservicio expone su propia documentación interactiva a través de Swagger. Una vez que los servicios estén corriendo, puedes acceder a la especificación de sus endpoints ingresando a:

* `http://localhost:[PUERTO_DEL_MICROSERVICIO]/swagger-ui.html`

**Asignatura:** Desarrollo Fullstack I  
**Integrantes:** Ignacio Mellado · Martina Molina · Vicente Arredondo  
