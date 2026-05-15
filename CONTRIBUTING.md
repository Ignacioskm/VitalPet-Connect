# Guía de contribución — VitalPet Connect

## Flujo de trabajo con Git

### Ramas
```
main          ← producción / entrega final
develop       ← integración del equipo
feat/ms-auth  ← rama por microservicio o feature
```

### Convención de commits
```
feat: nueva funcionalidad
fix:  corrección de bug
docs: documentación
test: pruebas
refactor: refactorización
```

### Ejemplos
```bash
git commit -m "feat(ms-auth): implementar generacion de JWT"
git commit -m "fix(ms-appointments): corregir validacion de horario"
git commit -m "docs(ms-pets): agregar endpoints en README"
```

## Cómo trabajar en un microservicio

```bash
git checkout develop
git pull origin develop
git checkout -b feat/ms-users-crud

# ... trabajar ...

git add .
git commit -m "feat(ms-users): CRUD completo de usuarios"
git push origin feat/ms-users-crud
# Crear Pull Request a develop en GitHub
```
