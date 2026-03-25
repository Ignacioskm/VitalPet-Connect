# 🐾 VitalPet Connect

> Ecosistema digital para la gestión clínica, administrativa y comunitaria de clínicas veterinarias.

**Asignatura:** Desarrollo Fullstack I  
**Integrantes:** Ignacio Mellado · Martina Molina · Vicente Arredondo  

---

## 🏗️ Arquitectura

Arquitectura basada en **microservicios** con Spring Boot, orquestados por un API Gateway y un servidor de descubrimiento Eureka.

```
vitalpet-connect/
├── infrastructure/          # Eureka Server + API Gateway
├── ms-auth/                 # Seguridad y JWT
├── ms-users/                # Perfiles y roles
├── ms-pets/                 # Registro de mascotas
├── ms-clinical-history/     # Historial clínico
├── ms-appointments/         # Gestión de citas
├── ms-staff/                # Personal veterinario
├── ms-payments/             # Pagos y facturación
├── ms-adoptions/            # Flujo de adopciones
├── ms-notifications/        # Alertas y recordatorios
└── ms-branches/             # Sedes y sucursales
```

## 🚀 Cómo levantar el proyecto

### Prerrequisitos
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0+

### Pasos
```bash
# 1. Levantar infraestructura (BD + Eureka + Gateway)
docker-compose up -d

# 2. Levantar en orden: primero infraestructura, luego servicios
cd infrastructure/eureka-server && mvn spring-boot:run
cd infrastructure/api-gateway  && mvn spring-boot:run

# 3. Luego cualquier microservicio
cd ms-users && mvn spring-boot:run
```

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
| ms-branches | 8090 | Sedes y sucursales | 🟢 Baja |
| eureka-server | 8761 | Service Discovery | — |
| api-gateway | 8080 | Puerta de entrada | — |

## 👥 Integrantes y responsabilidades

- **Ignacio Mellado** — 
- **Martina Molina** — 
- **Vicente Arredondo** — 

---
