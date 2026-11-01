# Warm-up project - Insurance Claims Api
REST API for managing insurance customers, policies and claims.

Claims go through a review workflow (NEW → IN_REVIEW → APPROVED / REJECTED).

## Technologies
Java 25, Spring Boot 4.1.1, JPA/Hibernate, PostgreSQL, Flyway, Docker,
Testcontainers, Mockito, MockMvc, Maven

### Executing program

```
docker compose up -d --build
```
The application runs on `http://localhost:8080`

### API documentation
Api documentation: `http://localhost:8080/swagger-ui/index.html`