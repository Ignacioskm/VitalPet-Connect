#!/bin/bash
# =================================================================
# Script para inicializar el repositorio GitHub de VitalPet Connect
# Ejecutar SOLO UNA VEZ desde la raíz del proyecto
# =================================================================

echo "🐾 Inicializando VitalPet Connect en GitHub..."

# 1. Inicializar git
git init
git branch -M main

# 2. Primer commit con toda la estructura
git add .
git commit -m "feat: initial project structure - VitalPet Connect

- 10 microservicios Spring Boot configurados
- Eureka Server (service discovery)
- API Gateway (Spring Cloud Gateway)
- docker-compose con 10 bases de datos MySQL aisladas
- Estructura de paquetes MVC por microservicio"

# 3. Conectar con GitHub (reemplazar con la URL real del repo)
# git remote add origin https://github.com/TU_USUARIO/vitalpet-connect.git
# git push -u origin main

echo ""
echo "✅ Listo! Ahora ejecuta:"
echo "   git remote add origin https://github.com/TU_USUARIO/vitalpet-connect.git"
echo "   git push -u origin main"
