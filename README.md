# QuickPay

Create payments one at a time, or bulk-upload them from a file. Spring Boot backend + Angular frontend.

```
QuickPay/
├── payments/       # Backend (Maven multi-module, Java, Spring Boot)
│   ├── domain/         # pure Java: models, validators, use cases
│   └── application/    # controllers, JPA, security (JWT), Liquibase
├── frontend/       # Angular app (standalone components)
├── docs/           # extra notes (auth tokens)
├── Dockerfile                  # production image (Angular + backend in one jar)
├── docker-compose.dev.yaml     # dev mode: live-reload frontend
└── docker-compose.prod.yaml    # production mode: single container on :8080
```

More detail: [`payments/README.md`](payments/README.md), [`frontend/README.md`](frontend/README.md),
[`FRONTEND-CONVENTIONS.md`](FRONTEND-CONVENTIONS.md), [`JWT-EXPLAINED.md`](JWT-EXPLAINED.md).

## Features

- Login with JWT (short-lived access token + refresh token, automatic refresh)
- List, view and create payments
- Bulk upload payments from a file, optionally using a template, with per-row error reporting
- Manage upload templates (users with the `TEMPLATE` role)

## Demo users

| User  | Password   | Roles           | Access                                              |
|-------|------------|-----------------|-----------------------------------------------------|
| alice | `alice123` | PAYMENT         | `/api/payments/**`                                  |
| admin | `admin123` | TEMPLATE, ADMIN | payments, `/api/templates/**`, `/api/auth/revoke/*` |

## Requirements

- Java 17+ (the Docker images use Java 25)
- Node 24 and Angular CLI 22 (for the frontend)
- Docker (for the Docker modes)
- `file-parser-core` installed in your local Maven repo (it is not on Maven Central; run `mvn install` in the file-parser project once)

## Ways to run

### 1. Local (backend + frontend separately)

```bash
# Terminal 1: backend on http://localhost:8080 (H2 file database)
cd payments
./mvnw spring-boot:run -pl application

# Terminal 2: frontend on http://localhost:4200
cd frontend
npm install
ng serve
```

`ng serve` proxies `/api` to `http://localhost:8080`, so there is no CORS setup.

### 2. Local app + Postgres in Docker

```bash
cd payments
docker compose -f local/docker-compose.yaml up -d
./mvnw spring-boot:run -pl application -Dspring-boot.run.profiles=postgres,liquibase
```

Then start the frontend as above.

### 3. Docker dev mode (Postgres + backend + live-reload frontend)

```bash
cd payments && ./mvnw package && cd ..        # builds the jar the backend image copies
docker compose -f docker-compose.dev.yaml up --build
```

Open http://localhost:4200. Edits in `frontend/` reload automatically.

### 4. Docker production mode (one container)

```bash
docker compose -f docker-compose.prod.yaml up --build
```

Docker builds Angular and the backend itself; the jar serves the UI and the API together on
http://localhost:8080. No `./mvnw package` step is needed, but `file-parser-core` must exist in
`~/.m2` (see Requirements).

### Backend only in Docker

```bash
cd payments
./mvnw package
docker compose up --build
```

This mode sets stricter settings through env vars (notes length 2, default currency `JOD`, bulk upload saves
valid rows). Defaults elsewhere: notes length 500, currencies `JOD,USD`, bulk save off. Validation rules
come from the backend, so the frontend shows its error messages instead of hardcoding limits.

## Tests

```bash
cd payments && ./mvnw test      # backend
cd frontend && ng test          # frontend
```

## API overview

| Method | Path                         | Notes                                           |
|--------|------------------------------|-------------------------------------------------|
| POST   | `/api/auth/login`            | `{username, password}` → `{token, refreshToken}` |
| POST   | `/api/auth/refresh`          | `{refreshToken}` → new tokens                   |
| POST   | `/api/auth/logout-all`       | signs out all devices                           |
| POST   | `/api/auth/revoke/{username}`| ADMIN only                                      |
| GET/POST | `/api/payments`            | list / create                                   |
| GET    | `/api/payments/{id}`         | 404 if missing                                  |
| POST   | `/api/payments/bulk`         | multipart: `file` + optional JSON `metadata` `{"TemplateName": "..."}` |
| GET/POST | `/api/templates`           | list / create (409 if duplicate)                |
| GET    | `/api/templates/name/{name}`, `/api/templates/id/{id}` | 404 if missing   |
| PUT/DELETE | `/api/templates/{name}`  | update / delete                                 |

Errors are plain text: 400 validation, 401 bad/expired credentials, 403 wrong role, 404 not found, 409 duplicate.

## Troubleshooting

- **`COPY target/application-0.0.1-SNAPSHOT.jar` fails in Docker build:** the jar isn't built yet. Run
  `cd payments && ./mvnw package` first (not needed for the production mode).
- **Login returns 401 with an empty body:** wrong username or password.
- **Frontend can't reach the API:** make sure the backend is on port 8080 (or set `API_TARGET` for the dev proxy).
