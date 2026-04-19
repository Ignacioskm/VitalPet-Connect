# 🐾 VitalPet Connect - Ecosistema de Gestión Veterinaria

**VitalPet Connect** es una plataforma integral basada en una **Arquitectura de Microservicios** diseñada para la gestión de clínicas veterinarias distribuidas. El sistema utiliza un enfoque descentralizado donde cada servicio es responsable de una parcela específica del negocio, garantizando alta disponibilidad y escalabilidad.

---

## 🛠️ Tecnologías y Herramientas

### **Backend & Microservicios**
* **Java 17 & Spring Boot 3**: Núcleo de todos los servicios.
* **Spring Cloud Netflix Eureka**: Service Discovery para el registro y localización automática de instancias.
* **Spring Cloud OpenFeign**: Comunicación declarativa y simplificada entre microservicios (HTTP Client).
* **Spring Data JPA**: Gestión de persistencia y acceso a datos mediante ORM.

### **Infraestructura & Datos**
* **Docker & Docker Compose**: Contenerización de servicios y orquestación de bases de datos.
* **MySQL 8**: Motor de base de datos relacional (una instancia independiente por microservicio).
* **Lombok**: Biblioteca para reducir el código repetitivo como Getters, Setters y Constructores.

---

## 🏗️ Arquitectura del Sistema

El ecosistema se divide en servicios especializados que se comunican mediante peticiones REST síncronas validadas:

| Microservicio | Responsabilidad Principal | Comunicación (Feign / Lógica) |
| :--- | :--- | :--- |
| **ms-branches** | Gestión de sedes y ciudades. | Provee validación a Staff y Appointments. |
| **ms-users** | Gestión de perfiles y roles. | Provee validación a Auth y Pets. |
| **ms-staff** | Personal médico y horarios. | Consulta a **ms-branches** para validar sedes. |
| **ms-pets** | Identidad de mascotas. | Consulta a **ms-users** para validar dueños. |
| **ms-auth** | Seguridad y JWT. | Consume datos de **ms-users** para login. |
| **ms-clinical** | Historias médicas y recetas. | Valida Pets y Staff antes de registrar atenciones. |
| **ms-appointments** | Agenda de citas. | Valida disponibilidad en Branches y Staff. |
| **ms-payments** | Cobros y transacciones. | Orquestado por Appointments al finalizar citas. |
| **ms-notifications** | Alertas (Email/Push). | Centraliza mensajes de todo el sistema. |
| **ms-adoptions** | Flujos de adopción. | Actualiza la propiedad (owner_id) en **ms-pets**. |

---

## 📊 Diagrama de Base de Datos (Relación Lógica)

A pesar de que cada microservicio cuenta con su propia base de datos física para mantener la independencia, el siguiente diagrama representa la **relación lógica global** de las entidades para facilitar la comprensión del dominio completo:

[Diagrama.pdf](https://github.com/user-attachments/files/26875374/Diagrama.pdf)


## 🚀 Guía de Inicio Rápido

Sigue estos pasos para poner en marcha el entorno de desarrollo en tu máquina local:

### **1. Levantar la Infraestructura (Docker)**
Para no configurar MySQL manualmente por cada servicio, utiliza el archivo `docker-compose.yml` que levanta los contenedores necesarios:
bash
docker-compose up -d

### **2. Servidor de Descubrimiento (Eureka Server)
Es el componente crítico que permite que los servicios se encuentren entre sí:

Navega a la carpeta del proyecto eureka-server/.

Ejecuta el comando: ./mvnw spring-boot:run (o usa tu IDE).

Accede al panel de control en: http://localhost:8761.

### **3. Ejecución de Microservicios
Una vez Eureka esté "UP", levanta los servicios esenciales en este orden de dependencia lógica:

## 📋 Microservicios

| Servicio | Puerto | Responsabilidad | Dificultad |
|---|---|---|---|
| ms-auth | 8081 | JWT y seguridad | 🔴 Alta |
| ms-users | 8082 | CRUD de usuarios | 🟢 Baja |
| ms-pets | 8083 | Registro de mascotas | 🟢 Baja |
| ms-clinical-history | 8084 | Historial clínico | 🟡 Media |
| ms-appointments | 8085 | Agenda y citas | 🔴 Alta |
| ms-staff | 8086 | Personal y especialidades | 🟢 Baja |
| ms-payments | 8087 | Pagos y transacciones | 🟡 Media |
| ms-adoptions | 8088 | Flujo de adopciones | 🟡 Media |
| ms-notifications | 8089 | Alertas y recordatorios | 🟡 Media |
| ms-branch | 8090 | Sedes y sucursales | 🟢 Baja |
| eureka-server | 8761 | Service Discovery | — |
| api-gateway | 8080 | Puerta de entrada | — |

## 🧪 Pruebas y Validación (Postman)
El sistema implementa validaciones cruzadas. Un ejemplo del flujo de trabajo es la creación de un miembro del Staff:

POST a /api/branches: Crea una sucursal (ej: ID 1 en Viña del Mar).

POST a /api/staff: Envía el JSON de creación incluyendo branchId: 1.

ms-staff intercepta la petición y contacta a ms-branches vía OpenFeign.

Si la sucursal existe y está activa, el registro se guarda junto con su lista de horarios.

Si la sucursal no existe, el sistema retorna un error descriptivo (400 Bad Request).

## 📝 Notas de Desarrollo e Infraestructura
Persistencia: Se utiliza spring.jpa.hibernate.ddl-auto: update para mantener los datos manuales.

Semilla de datos: Los archivos data.sql usan INSERT IGNORE para cargar roles, ciudades y especialidades automáticamente.

Roadmap: Puedes seguir el progreso detallado y los criterios de aceptación en la pestaña Projects de este repositorio de GitHub.
