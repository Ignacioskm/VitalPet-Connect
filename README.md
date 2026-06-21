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

## 🚀 4. Despliegue con Docker

### 4.1. Prerrequisitos

*   **Docker** (versión 24+)
*   **Docker Compose** (versión 2.20+)
*   **Git** (para clonar el repositorio)
*   **Mínimo 16 GB de RAM** recomendado para ejecutar todos los servicios

### 4.2. Configuración Inicial

1.  **Clona el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    cd VitalPet-Connect
    ```

2.  **Configura las variables de entorno:**
    El archivo `.env` en la raíz del proyecto contiene las credenciales sensibles. Asegúrate de que tenga el siguiente contenido:
    ```env
    DB_ROOT_PASSWORD=root
    JWT_SECRET=tu_secreto_jwt_aqui
    ```

    > **Nota:** Si el JWT_SECRET contiene signos `$`, Docker puede interpretarlos como variables. Para evitarlo, usa un secreto sin caracteres especiales o escapa los `$`.

### 4.3. Levantar el Proyecto

**Opción 1: Solo servicios esenciales (recomendado para desarrollo rápido)**
Levanta únicamente 6 contenedores: MySQL, Eureka, Gateway, Auth, Users y Pets.
```bash
docker compose up -d
```

**Opción 2: Todos los servicios (10 microservicios completos)**
```bash
docker compose --profile full up -d
```

**Primera vez (incluye build de imágenes):**
```bash
docker compose --profile full up -d --build
```

> El flag `--build` fuerza la reconstrucción de las imágenes. Solo es necesario la primera vez o cuando hagas cambios en el código.

### 4.4. Orden de Inicialización (Automático)

No necesitas preocuparte por el orden: los healthchecks y `depends_on` se encargan de iniciar los servicios en la secuencia correcta:

```
db-master (MySQL) ──> eureka-server ──> api-gateway
                        │
                        ├──> ms-auth
                        ├──> ms-users
                        ├──> ms-pets
                        ├──> ms-branches
                        ├──> ms-clinical-history
                        ├──> ms-appointments
                        ├──> ms-staff
                        ├──> ms-payments
                        ├──> ms-adoptions
                        └──> ms-notifications
```

Cada microservicio espera a que MySQL esté saludable y Eureka esté corriendo antes de iniciar.

### 4.5. Verificar el Estado

Una vez levantado, puedes verificar que todo funcione:

```bash
# Ver contenedores activos
docker ps

# Ver uso de recursos (cada servicio tiene límite de 768MB RAM / 1 CPU)
docker stats

# Ver qué servicios están registrados en Eureka
curl http://localhost:8761/eureka/apps | grep -oP '<name>\K[^<]+'
```

**Salida esperada (Opción 2):**
```
API-GATEWAY
MS-ADOPTIONS
MS-APPOINTMENTS
MS-AUTH
MS-BRANCHES
MS-CLINICAL-HISTORY
MS-NOTIFICATIONS
MS-PAYMENTS
MS-PETS
MS-STAFF
MS-USERS
```

### 4.6. Comandos Útiles

```bash
# Detener todos los servicios
docker compose down

# Detener y eliminar volúmenes (borra datos de BD)
docker compose down -v

# Ver logs de un servicio específico
docker compose logs -f ms-pets

# Reconstruir un solo servicio
docker compose build ms-pets

# Escalar (iniciar solo servicios default sin perfil full)
docker compose up -d
```

### 4.7. Levantamiento Manual (Alternativa sin Docker)

Si prefieres ejecutar los microservicios directamente con Maven sin Docker:

1.  **Base de Datos:** Asegúrate de tener MySQL corriendo localmente. Puedes levantar solo la BD con:
    ```bash
    docker compose up -d db-master
    ```
2.  **Infraestructura Base:** Inicia `eureka-server` y espera a que levante completamente.
3.  **Seguridad y Enrutamiento:** Inicia `api-gateway` y `ms-auth`.
4.  **Microservicios de Negocio:** Levanta el resto (`ms-users`, `ms-pets`, `ms-branches`, etc.) en cualquier orden.

---

## 🛤️ 5. Pruebas y Demostración.

Para ver el flujo completo de cómo interactúan los microservicios entre sí, puedes revisar nuestro documento detallado.

**[Ver documento de Flujo de Pruebas (PDF)](./docs/Funcionamiento_correcto_de_todos_los_MS..pdf)**

## 📚 6. Documentación API (Swagger)

Una vez que los servicios estén corriendo, la documentación interactiva de **todos los microservicios** está centralizada en el API Gateway:

*   **Swagger UI unificado:** [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

También puedes acceder a las especificaciones individuales directamente desde el Gateway:

| Microservicio | Endpoint OpenAPI |
|---|---|
| Auth | `/api/auth/v3/api-docs` |
| Users | `/api/user/v3/api-docs` |
| Pets | `/api/pets/v3/api-docs` |
| Clinical History | `/api/clinical/v3/api-docs` |
| Appointments | `/api/appointments/v3/api-docs` |
| Staff | `/api/staff/v3/api-docs` |
| Payments | `/api/payments/v3/api-docs` |
| Adoptions | `/api/adoptions/v3/api-docs` |
| Notifications | `/api/notifications/v3/api-docs` |
| Branches | `/api/branches/v3/api-docs` |

**Asignatura:** Desarrollo Fullstack I  
**Integrantes:** Ignacio Mellado · Martina Molina · Vicente Arredondo  
