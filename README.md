# Warm-up project: Insurance Claims API

Cookie-based JWT authentication with refresh token rotation, built on Spring Security 7.

REST API for insurance customers, policies and claims.
Claims go through a review workflow: `NEW → IN_REVIEW → APPROVED / REJECTED`.

## Security
- Access token: JWT (HS256, Nimbus) in an `HttpOnly`, `SameSite=Strict` cookie, validated by the OAuth2 resource server
- Refresh token: separate cookie scoped to `/auth`, stored hashed, rotated on every use; reuse of a rotated token revokes the whole token family
- CSRF: signed double-submit token bound to the token family
- Roles `ADMIN` / `CLIENT`; clients see only their own customer data, policies and claims
- Initial admin created from environment variables (skipped when unset)

## Tech
Java 25, Spring Boot 4.1.1, Spring Security 7, PostgreSQL, Flyway, Testcontainers

## Run
```
cp .env.example .env
docker compose up -d --build
```
API: `http://localhost:8080` · Docs: `http://localhost:8080/swagger-ui/index.html`

Admin from `.env.example`: `sigma@sigma.test` / `password`
