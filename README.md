# Assemble CRM — Backend

CRM multi-tenant para pequeñas empresas. Backend en **Java 21 + Spring Boot 3.4**, **PostgreSQL**, **Spring Security + JWT** (con refresh tokens rotativos), **Flyway**, **Bean Validation** y arquitectura por capas (controller / service / repository / entity / dto / mapper).

> El frontend (Angular) se construye por separado y consume esta API en `http://localhost:8080/api/v1`.

## Requisitos

- Java 21 (Temurin recomendado)
- Maven 3.9+
- PostgreSQL 16 (o Docker)

## 1. Levantar con Docker

El `docker-compose.yml` construye el backend y levanta PostgreSQL:

```bash
docker compose up -d --build
```

La API queda disponible en `http://localhost:8080` y PostgreSQL en
`localhost:5432`. Antes de usarlo fuera de desarrollo, copia `.env.example`
a `.env` y cambia `POSTGRES_PASSWORD` y `APP_JWT_SECRET`.

Si usas tu propio PostgreSQL, ajusta las variables (ver más abajo) o `application.yml`.

## 2. Variables de entorno

Copia `.env.example` y ajusta lo necesario. Las claves nunca van hardcodeadas: se leen como `${VARIABLE}`.

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/assemble_crm` |
| `DB_USERNAME` | Usuario DB | `assemble` |
| `DB_PASSWORD` | Password DB | `assemble` |
| `JWT_SECRET` | Secreto HS256 (min. 32 bytes) | *(definir en prod)* |
| `APP_SEED_ENABLED` | Carga datos demo al iniciar | `true` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos | `http://localhost:4200` |

## 3. Ejecutar

```bash
mvn spring-boot:run
```

Al primer arranque, Flyway crea el esquema (`V1__init_schema.sql`) y el `SeedRunner`:
- siempre asegura los 6 roles del sistema con sus permisos,
- si `APP_SEED_ENABLED=true`, crea una empresa demo con datos de ejemplo.

### Credenciales demo

```
Email:    admin@assemblecrm.com
Password: Admin123*
```

## 4. Documentación de la API

Con la app corriendo: **Swagger UI** en `http://localhost:8080/swagger-ui.html`.

## Endpoints principales (`/api/v1`)

| Módulo | Rutas |
|---|---|
| Auth | `POST /auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/forgot-password`, `/auth/reset-password`, `/auth/change-password` |
| Usuarios | `GET/POST/PUT /users`, `PATCH /users/{id}/status`, `DELETE /users/{id}` |
| Clientes | `GET/POST/PUT /customers`, `DELETE /customers/{id}` (archiva), sub-recursos `/{id}/contacts`, `/{id}/opportunities`, `/{id}/notes`, `/{id}/timeline` |
| Contactos | `GET/POST/PUT/DELETE /contacts` |
| Leads | `GET/POST/PUT /leads`, `PATCH /leads/{id}/status`, `POST /leads/{id}/convert-to-customer` |
| Oportunidades | `GET/POST/PUT/DELETE /opportunities`, `PATCH /{id}/stage`, `PATCH /{id}/status` |
| Tareas | `GET/POST/PUT/DELETE /tasks`, `PATCH /{id}/status` |
| Notas | `GET/POST/DELETE /notes` |
| Actividades | `GET/POST /activities` |
| Reportes | `GET /reports/dashboard`, `/pipeline`, `/leads-by-source`, `/sales-performance`, `/tasks-summary`, `/pipeline/export` (CSV) |
| Settings | `GET/PUT /settings/company`, `GET /settings/sources`, `GET /settings/categories` |

## Roles y permisos

Roles: `SUPER_ADMIN`, `ADMIN`, `MANAGER`, `SALES`, `SUPPORT`, `VIEWER`.
Permisos: `VIEW_DASHBOARD`, `MANAGE_CUSTOMERS`, `MANAGE_CONTACTS`, `MANAGE_LEADS`, `MANAGE_OPPORTUNITIES`, `MANAGE_TASKS`, `VIEW_REPORTS`, `MANAGE_USERS`, `CONFIGURE_SYSTEM`.
La autorización se aplica por método con `@PreAuthorize("hasAuthority('...')")`.

## Multi-tenancy

Cada tabla de negocio lleva `company_id`. El `company_id` se toma siempre del token autenticado vía `SecurityUtils.currentCompanyId()`, nunca del request, lo que aísla los datos entre empresas.

## Formato de respuesta

- Éxito: `{ "success": true, "message": "...", "data": {...} }`
- Error: `{ "success": false, "message": "...", "errors": [...] }`
- Paginado: `{ "success": true, "data": [...], "page", "size", "totalElements", "totalPages" }`

## Tests

```bash
mvn test
```

Los tests usan H2 en memoria (`application-test.yml`).

## Nota sobre el build

Este proyecto no se compiló en el entorno de generación (sin acceso a Maven Central). Compílalo localmente con `mvn clean package`; el JAR queda en `target/`.
