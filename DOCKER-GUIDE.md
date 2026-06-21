# 🐳 Guía Docker — VitalPet Connect

> Una guía paso a paso para entender Docker, los Dockerfiles y Docker Compose usando este proyecto como ejemplo. El objetivo es que aprendas los fundamentos y puedas dockerizar tus propios proyectos en el futuro.

---

## Índice

1. [¿Qué es Docker?](#1-qué-es-docker)
2. [Anatomía del Dockerfile](#2-anatomía-del-dockerfile)
3. [Docker Compose paso a paso](#3-docker-compose-paso-a-paso)
4. [Cómo dockerizar un proyecto nuevo](#4-cómo-dockerizar-un-proyecto-nuevo)
5. [Comandos esenciales (Cheat Sheet)](#5-comandos-esenciales-cheat-sheet)
6. [Buenas prácticas](#6-buenas-prácticas)

---

## 1. ¿Qué es Docker?

Docker es una herramienta que permite **empaquetar una aplicación con todo lo que necesita para funcionar** (librerías, herramientas, configuraciones) en un objeto llamado **contenedor**.

### Conceptos clave

| Concepto | Explicación |
|---|---|
| **Imagen** | Plantilla lista para ejecutarse. Comparable a un ISO o a un snapshot de una máquina virtual. |
| **Contenedor** | Una instancia en ejecución de una imagen. Puedes tener varios contenedores desde una misma imagen. |
| **Dockerfile** | Receta que describe cómo construir una imagen paso a paso. |
| **Docker Compose** | Herramienta para definir y ejecutar múltiples contenedores que trabajan juntos. |
| **Volumen** | Carpeta compartida entre el host y el contenedor para persistir datos. |
| **Red** | Conexión virtual que permite a los contenedores comunicarse entre sí. |

### Imagen vs Contenedor (analogía)

```
Dockerfile  ──build──>  Imagen  ──run──>  Contenedor
  (receta)            (molde)           (tarta horneada)
```

---

## 2. Anatomía del Dockerfile

Vamos a analizar el Dockerfile de `ms-auth` línea por línea. Todos los microservicios usan exactamente la misma estructura.

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-Xmx256m", "-Xms256m", "-jar", "app.jar"]
```

### Explicación línea por línea

#### Stage 1: Build (compilación)

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
```
- **`FROM`** define la imagen base sobre la que construirás.
- `maven:3.9-eclipse-temurin-17` es una imagen oficial que incluye Maven 3.9 y Java 17 (Temurin = build de Eclipse Adoptium).
- **`AS build`** le da un nombre a esta etapa del build para referenciarla después (multi-stage build).

```dockerfile
WORKDIR /app
```
- Crea la carpeta `/app` dentro del contenedor y la establece como directorio de trabajo. Todos los comandos siguientes se ejecutarán ahí.

```dockerfile
COPY pom.xml .
```
- Copia **solo** el `pom.xml` (el archivo de dependencias de Maven) al contenedor.
- Se separa del `COPY src` para aprovechar el **caché de capas de Docker**. Si el `pom.xml` no cambia, esta capa se reusa y no se descargan las dependencias de nuevo.

```dockerfile
RUN mvn dependency:go-offline
```
- Descarga **todas las dependencias** del proyecto (Spring Boot, Spring Cloud, MySQL connector, etc.) y las guarda en caché dentro de la imagen.
- Esto hace que la compilación sea mucho más rápida la próxima vez (porque ya están descargadas).

```dockerfile
COPY src ./src
```
- Ahora sí copia el código fuente de la aplicación.

```dockerfile
RUN mvn package -DskipTests
```
- Compila el proyecto, empaqueta todo en un archivo `.jar` y lo deja en `target/`.
- `-DskipTests` omite los tests para que el build sea más rápido (puedes quitarlo si quieres ejecutar tests en el build).

#### Stage 2: Runtime (ejecución)

```dockerfile
FROM eclipse-temurin:17-jre
```
- **Nueva etapa** desde cero. Esta imagen solo contiene el JRE (Java Runtime Environment), sin Maven ni herramientas de compilación.
- La imagen pesa ~180MB, mucho menos que la de Maven (~700MB).
- **Esto es el multi-stage build** — solo lo necesario para ejecutar, no para compilar.

```dockerfile
WORKDIR /app
```

```dockerfile
COPY --from=build /app/target/*.jar app.jar
```
- **`--from=build`** copia el archivo JAR desde la etapa anterior llamada `build`.
- Solo el JAR terminado viaja a esta imagen final — nada de Maven, nada de código fuente.

```dockerfile
ENTRYPOINT ["java", "-Xmx256m", "-Xms256m", "-jar", "app.jar"]
```
- **`ENTRYPOINT`** define el comando que se ejecutará cuando el contenedor arranque.
- `-Xmx256m` y `-Xms256m` limitan la memoria del heap de Java a 256MB (máximo e inicial). Sin esto, la JVM tomaría tanta RAM como el host tenga disponible, lo que puede saturar el sistema.
- Se usa formato JSON (`["comando", "arg1", "arg2"]`) en vez de shell simple porque evita problemas con señales del sistema.

### ¿Por qué multi-stage?

```
Sin multi-stage:
  Imagen con Maven + JDK = ~700MB  (Maven solo sirve para compilar, no para ejecutar)

Con multi-stage:
  Imagen solo con JRE = ~180MB  (mucho más pequeña y rápida de descargar/transferir)
```

---

## 3. Docker Compose paso a paso

Docker Compose permite definir todos los contenedores, sus configuraciones y cómo se relacionan en un solo archivo YAML.

Veamos las partes clave del `docker-compose.yml` de este proyecto.

### 3.1. Estructura básica

```yaml
services:              # Lista de contenedores
  nombre-del-servicio: # Nombre interno que usan otros servicios para referenciarlo
    image: mysql:8.0   # Imagen a usar (no construida, descargada de Docker Hub)
    build: ./ms-auth   # O bien, ruta al Dockerfile para construir la imagen
    ports:             # Mapeo de puertos: host:contenedor
      - "8080:8080"
    environment:       # Variables de entorno dentro del contenedor
      SPRING_PROFILES_ACTIVE: docker
    depends_on:        # Dependencias: espera a que otro servicio esté listo
      - eureka-server
    networks:          # Redes a las que pertenece el contenedor
      - vitalpet-network

networks:              # Definición de redes
  vitalpet-network:
    driver: bridge
```

### 3.2. `image` vs `build`

- **`image: mysql:8.0`**: Usa una imagen preconstruida desde Docker Hub (o un registro privado). No necesitas un Dockerfile.
- **`build: ./ms-auth`**: Construye una imagen desde el Dockerfile que está en esa carpeta. Docker Compose ejecuta `docker build` automáticamente.

### 3.3. `ports`

```yaml
ports:
  - "8080:8080"
```

Formato: `"puerto_host:puerto_contenedor"`. Esto hace que el servicio sea accesible desde tu máquina en `localhost:8080`. Sin esto, el contenedor solo sería accesible desde otros contenedores en la misma red Docker.

### 3.4. `environment`

```yaml
environment:
  SPRING_PROFILES_ACTIVE: docker
  DB_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
```

- Las variables de entorno se inyectan dentro del contenedor.
- `${DB_ROOT_PASSWORD}` hace referencia al archivo `.env` en la raíz del proyecto. Docker Compose lee automáticamente el `.env` y expande las variables.

### 3.5. `depends_on` y `healthcheck`

```yaml
depends_on:
  db-master:
    condition: service_healthy
  eureka-server:
    condition: service_started
```

- `depends_on` por sí solo solo espera a que el otro contenedor **se haya creado**, no a que esté listo para recibir peticiones.
- Para esperar a que realmente esté listo, se necesita un **healthcheck** en el servicio del que dependes:

```yaml
db-master:
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval: 10s
    timeout: 5s
    retries: 10
    start_period: 30s
```

- `test`: comando que se ejecuta periódicamente para verificar salud. Código 0 = saludable.
- `interval`: cada cuánto se ejecuta el test.
- `retries`: cuántos fallos seguidos antes de marcar como no saludable.
- `start_period`: tiempo de gracia inicial para que el servicio arranque.

Luego el que depende puede usar `condition: service_healthy` para esperar a que el healthcheck pase.

### 3.6. `deploy.resources.limits`

```yaml
deploy:
  resources:
    limits:
      memory: 768M
      cpus: '1.0'
```

- **Crítico para evitar que tu PC se congele.** Sin estos límites, un contenedor con una fuga de memoria puede consumir toda la RAM del sistema.
- `memory: 768M`: el contenedor no podrá usar más de 768MB de RAM.
- `cpus: '1.0'`: el contenedor usará como máximo 1 núcleo de CPU.

### 3.7. `profiles`

```yaml
servicios-esenciales:
  # sin profiles → siempre se inician

servicios-opcionales:
  profiles:
    - full
  # solo se inician con: docker compose --profile full up
```

Los profiles permiten tener grupos de servicios que se inician bajo demanda. En este proyecto:
- **Default (sin profile):** db-master, eureka-server, api-gateway, ms-auth, ms-users, ms-pets
- **Full (`--profile full`):** los 7 microservicios restantes

### 3.8. Redes (`networks`)

```yaml
networks:
  vitalpet-network:
    driver: bridge
```

- **bridge**: red privada por defecto. Los contenedores en la misma red pueden verse entre sí por su nombre de servicio.
- Ejemplo: `ms-auth` se conecta a `jdbc:mysql://db-master:3306/vitalpet_auth` — el nombre `db-master` se resuelve gracias a que ambos están en `vitalpet-network`.

### 3.9. `.env` (variables de entorno)

Crear un archivo `.env` en la raíz del proyecto:

```env
DB_ROOT_PASSWORD=root
JWT_SECRET=mi_secreto_super_seguro
```

Docker Compose lo lee automáticamente y sustituye `${DB_ROOT_PASSWORD}` en el `docker-compose.yml`. Así evitas tener contraseñas hardcodeadas en el código.

---

## 4. Cómo dockerizar un proyecto nuevo

Aquí tienes una receta genérica para dockerizar **cualquier** proyecto.

### Paso 1: Elegir imagen base

Busca una imagen oficial y ligera para tu tecnología:

| Tecnología | Imagen base recomendada |
|---|---|
| Java / Spring Boot | `eclipse-temurin:17-jre` (o `-alpine` para más pequeño) |
| Node.js / Express | `node:20-alpine` |
| Python / Flask | `python:3.12-slim` |
| Go | `golang:1.22-alpine` |
| .NET | `mcr.microsoft.com/dotnet/aspnet:8.0` |

### Paso 2: Escribir el Dockerfile

```dockerfile
# === STAGE 1: Build ===
FROM imagen-de-compilacion AS build
WORKDIR /app
COPY package.json .
RUN npm install        # o: mvn dependency:go-offline
COPY . .
RUN npm run build      # o: mvn package

# === STAGE 2: Runtime ===
FROM imagen-de-ejecucion
WORKDIR /app
COPY --from=build /app/dist /app
EXPOSE 3000            # (opcional, solo documenta el puerto)
ENTRYPOINT ["node", "server.js"]
```

### Paso 3: Crear `.dockerignore`

```ignore
node_modules/
.git/
.env
*.log
dist/
target/
.idea/
```

Esto evita que archivos innecesarios se copien al contenedor durante el build, haciendo el proceso más rápido y seguro.

### Paso 4: Escribir `docker-compose.yml`

```yaml
services:
  mi-app:
    build: .
    ports:
      - "8080:3000"
    environment:
      NODE_ENV: production
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 256M
          cpus: '0.5'

  mi-base-de-datos:
    image: postgres:16-alpine
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready"]
      interval: 10s
      retries: 5

volumes:
  pgdata:
```

### Paso 5: Probar

```bash
# Construir y levantar
docker compose up -d --build

# Ver logs
docker compose logs -f

# Ver estado
docker compose ps
docker stats

# Detener
docker compose down
```

---

## 5. Comandos esenciales (Cheat Sheet)

### Gestión de imágenes

```bash
# Construir imagen desde Dockerfile
docker build -t mi-app:latest .

# Listar imágenes
docker images

# Eliminar imagen
docker rmi mi-app:latest

# Eliminar imágenes no usadas (liberar espacio)
docker image prune -a
```

### Gestión de contenedores

```bash
# Ejecutar contenedor (crear + iniciar)
docker run -d -p 8080:8080 --name mi-app mi-app:latest

# Listar contenedores activos (-a para todos, incluso detenidos)
docker ps -a

# Detener contenedor
docker stop mi-app

# Iniciar contenedor detenido
docker start mi-app

# Eliminar contenedor
docker rm mi-app

# Ver logs en tiempo real
docker logs -f mi-app

# Ejecutar comando dentro de un contenedor activo
docker exec -it mi-app bash

# Ver uso de recursos en vivo
docker stats
```

### Docker Compose

```bash
# Construir y levantar todos los servicios
docker compose up -d --build

# Levantar solo un servicio
docker compose up -d ms-auth

# Ver logs de todos los servicios
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f ms-pets

# Listar servicios y su estado
docker compose ps

# Detener y eliminar contenedores, redes y volúmenes
docker compose down

# Detener y eliminar también los volúmenes (borra datos de BD)
docker compose down -v

# Reconstruir una imagen sin afectar contenedores activos
docker compose build ms-auth

# Ejecutar un comando en un servicio en ejecución
docker compose exec ms-auth bash
```

### Limpieza

```bash
# Liberar espacio: contenedores, imágenes y caché no utilizados
docker system prune -a --volumes
```

> ⚠️ **Advertencia:** `system prune -a --volumes` borra **todo** lo que no esté en uso: imágenes sin contenedor asociado, contenedores detenidos, volúmenes huérfanos y caché de build. Úsalo cuando quieras empezar desde cero.

---

## 6. Buenas prácticas

### 6.1. Multi-stage build
Siempre separa la etapa de compilación de la de ejecución. La imagen final debe contener solo lo necesario para **correr** la app, no para compilarla. Esto reduce el tamaño de las imágenes drásticamente.

### 6.2. Ordenar COPY para aprovechar caché
```dockerfile
# Mal: si cambias una línea de código, se descargan todas las dependencias de nuevo
COPY . .
RUN npm install

# Bien: las dependencias se cachean por separado del código
COPY package.json .
RUN npm install
COPY . .
```

### 6.3. Siempre poner límites de recursos
Sin `deploy.resources.limits`, un contenedor con una fuga de memoria puede saturar tu PC (como te pasó a ti al inicio). Siempre define:

```yaml
deploy:
  resources:
    limits:
      memory: 512M
      cpus: '1.0'
```

### 6.4. Usar healthchecks
No confíes solo en `depends_on`. Sin healthcheck, un contenedor puede estar "corriendo" pero no listo. El healthcheck garantiza que realmente esté operativo antes de que otros se conecten.

### 6.5. No correr como root
En producción, agrega un usuario no root en el Dockerfile:

```dockerfile
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser
```

### 6.6. Usar `.dockerignore`
Siempre crea un `.dockerignore` para evitar enviar contextos pesados al Daemon de Docker. Esto acelera los builds y evita que archivos sensibles (como `.env`) terminen dentro de la imagen.

### 6.7. Variables de entorno, no valores hardcodeados
Usa `${VARIABLE}` en el `docker-compose.yml` y define los valores en un `.env`. Así puedes cambiar configuraciones sin modificar el código.

### 6.8. Un proceso por contenedor
Un contenedor debe ejecutar un solo proceso (ej: tu app Java). Si necesitas base de datos + app, usa dos servicios separados en Docker Compose. No metas todo en un mismo contenedor.

### 6.9. Etiquetar imágenes
Usa tags descriptivos, no solo `latest`:

```bash
docker build -t mi-app:1.0.0 .
docker build -t mi-app:abc123def .  # con hash de commit
```

### 6.10. Prefiere imágenes alpine/slim
Las variantes `alpine`, `slim` o `-jre` (en lugar de `-jdk`) son mucho más pequeñas y tienen menos superficie de ataque.

| Imagen | Tamaño |
|---|---|
| `openjdk:17` | ~470MB |
| `eclipse-temurin:17-jre` | ~180MB |
| `node:20` | ~350MB |
| `node:20-alpine` | ~130MB |

---

> **Recuerda:** Docker no es magia, es una herramienta. El objetivo es que tu aplicación se ejecute de manera predecible en cualquier entorno. Todo lo que aprendiste aquí aplica a proyectos en Java, Node.js, Python, Go, o cualquier tecnología que uses en el futuro.
