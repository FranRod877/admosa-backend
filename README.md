# AdMosa Backend

Backend de Spring Boot para la gestión segura de archivos por usuario, rol y área.

## Tecnologías
- Java 17
- Spring Boot 4.0.0
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Gradle

## Requisitos
- Java 17
- PostgreSQL 15+
- Git

## Configuración local de PostgreSQL
1. Instalar PostgreSQL.
2. Crear la base de datos:
   ```sql
   CREATE DATABASE admosa_db;
   ```
3. Ajustar credenciales si es necesario.

Variables de entorno recomendadas:
```bash
setx ADMOSA_DB_USER postgres
setx ADMOSA_DB_PASS postgres
```

## Ejecutar el proyecto
```bash
cd C:\Users\franr\workspace\admosa-backend
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot
gradlew.bat bootRun
```

## Estructura sugerida del proyecto
- `src/main/java/com/admosa/backend` → código de la aplicación
- `src/main/resources` → configuración y propiedades

## Ramas git
- `main` → releases estables
- `develop` → trabajo activo

## Siguiente paso
- Crear entidades: `Usuario`, `Rol`, `Area`, `Archivo`
- Crear repositorios y servicios
- Definir JWT y seguridad por permisos
- Exponer endpoints REST para autenticación y gestión de archivos
