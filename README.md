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
3. Si tu usuario/contraseña no son `postgres`/`postgres`, hay dos formas de indicarlos (no hace falta tocar `application.properties`):
   - **Variables de entorno:** `setx ADMOSA_DB_USER postgres` y `setx ADMOSA_DB_PASS tu_password` (abrir una terminal nueva para que tomen efecto).
   - **Archivo local (recomendado si corres el proyecto desde varias carpetas/IDEs):** crea `ADMOSA_DB_PASS=tu_password` (y `ADMOSA_DB_USER=...` si aplica) en un archivo `.admosa-backend.properties` dentro de tu carpeta de usuario (`%USERPROFILE%\.admosa-backend.properties` en Windows). El proyecto lo lee automáticamente si existe; si no existe, no pasa nada y usa los valores por defecto. Nunca se sube al repo.

## Ejecutar el proyecto
```bash
cd C:\Users\franr\workspace\admosa-backend
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot
gradlew.bat bootRun
```

## Estructura del proyecto
- `domain` → entidades JPA (`Usuario`, `Rol`, `Area`, `Archivo`, `HistorialAccion`)
- `repository` → repositorios Spring Data JPA
- `security` → JWT (`JwtService`, filtro, `UserDetailsService`)
- `service` → lógica de negocio, incluida `FileAccessPolicy` (reglas de alcance por rol/área)
- `web` → controladores REST y manejo global de errores
- `seed` → `DataSeeder`, crea los usuarios de prueba al arrancar sobre una BD vacía
- `db/migration` → migraciones Flyway (`V1__schema.sql`)

## Usuarios de prueba
Creados automáticamente al arrancar (una sola vez, si la tabla `usuarios` está vacía). Contraseña `Password123!` para todos:

| Email | Rol | Área |
|---|---|---|
| admin@admosa.com | Administrador | — |
| gerente@admosa.com | Gerente | gestiona Ventas y Tecnología |
| jefe@admosa.com | Jefe de área | Ventas |
| usuario@admosa.com | Usuario estándar | Ventas |
| usuario2@admosa.com | Usuario estándar | Tecnología |

## Endpoints principales
- `POST /api/auth/login`
- `GET/POST /api/files`, `GET /api/files/{id}/download`, `DELETE /api/files/{id}`
- `GET /api/history`
- `GET/PATCH /api/users/{id}`, `GET /api/areas` (solo Administrador)

## Ramas git
- `main` → releases estables
- `develop` → trabajo activo
