# QuickPay

Multi-module Maven project (Spring Boot 4.1, Java 17) for creating payments and bulk-uploading them from files.

```
quickpay/
├── payments/            # BACKEND (Maven parent: domain + application)
│   ├── domain/          # pure Java: models, validators, use cases
│   └── application/     # Spring Boot: controllers, JPA, security, Liquibase
└── frontend/            # FRONTEND (Angular) — to be created
```

## Rules for working in this repo

- **Do NOT modify the backend (`payments/`) unless the user explicitly asks.** The user owns it and is
  comfortable with it. If the frontend needs something the backend lacks (e.g. CORS, a new endpoint),
  say so and propose it. Do not make the change.
- The user is **new to Angular** (knows only the very basics). Frontend code must stay simple and readable.
- The backend **will be extended** (new endpoints, new fields, new roles). The frontend must make that cheap.

## Backend facts (for the frontend)

Runs on `http://localhost:8080`. Run: `cd payments && ./mvnw spring-boot:run -pl application`
(default profiles `H2,liquibase`). Test: `cd payments && ./mvnw test`.

### Ways to run the backend (the frontend must work with all of them)

| Mode | Command (from `payments/`) | Database |
|------|----------------------------|----------|
| Local, default | `./mvnw spring-boot:run -pl application` | H2 file (`data/mydb`), profiles `H2,liquibase` |
| Local app + Docker DB | `docker compose -f local/docker-compose.yaml up -d`, then run the app with `-Dspring-boot.run.profiles=postgres,liquibase` | Postgres 16 on `localhost:5432` |
| Full Docker | `./mvnw package`, then `docker compose up --build` | Postgres container, app on `localhost:8080` |

- In every mode the API is at `http://localhost:8080`, so **the frontend never needs to know which database is used**.
- Full-Docker mode overrides behaviour via env vars in `payments/docker-compose.yaml`:
  `PAYMENTS_NOTES_DEFAULT-LENGTH=2`, `PAYMENTS_CURRENCY_DEFAULT-CURRENCY=JOD`, `PAYMENTS_UPLOUD-BULK_SAVE=true`.
  Defaults (`application.properties`) are notes length 500, currencies `JOD,USD`, bulk save `false`.
  So **validation limits and bulk-save behaviour differ between modes**: never hardcode these rules
  in the frontend; show the backend's error messages instead.
- Data differs per database (H2 file vs. Postgres volume), so don't assume seed data exists.

**Auth:** HTTP Basic, CSRF disabled, **no CORS config**. In-memory users:

| user  | password   | role     | can access                          |
|-------|------------|----------|-------------------------------------|
| alice | `alice123` | PAYMENT  | `/api/payments/**`                  |
| admin | `admin123` | TEMPLATE | `/api/payments/**` and `/api/templates/**` |

**Endpoints**

| Method | Path                         | Body / notes                                              |
|--------|------------------------------|-----------------------------------------------------------|
| GET    | `/api/payments`              | list of PaymentResponse                                   |
| GET    | `/api/payments/{id}`         | 404 if missing                                            |
| POST   | `/api/payments`              | PaymentRequest → 201 PaymentResponse                      |
| POST   | `/api/payments/bulk`         | multipart: part `file` + optional JSON part `metadata` `{"TemplateName": "..."}` → BulkUploadResponse |
| GET    | `/api/templates`             | list of TemplateResponse                                  |
| GET    | `/api/templates/name/{name}` | 404 if missing                                            |
| GET    | `/api/templates/id/{id}`     | 404 if missing                                            |
| POST   | `/api/templates`             | TemplateRequest → 201 (409 if duplicate)                  |
| PUT    | `/api/templates/{name}`      | TemplateRequest                                           |
| DELETE | `/api/templates/{name}`      | 204                                                       |

**Shapes** (see `payments/application/src/main/java/org/example/payments/resource/`)

- PaymentRequest: `senderAccount, receiverAccount, amount, currency, status, notes, creditorName`
- PaymentResponse: `id, senderAccount, receiverAccount, amount, currency, status, createdAt, notes`
- TemplateRequest/Response: `name, requestList: [{name, length, required}]`
- BulkUploadResponse: `total, success, failure, rowErrors: {rowNumber: [messages]}, payments`

**Errors** are plain-text bodies (not JSON): validation → 400, duplicate template → 409,
template not found → 404, bad credentials → 401, wrong role → 403.

**Quirk:** the bulk request JSON key is `TemplateName` (capital T) — it comes from the Lombok field name.

## Frontend plan (Angular)

Location: `frontend/` at repo root. Angular CLI 22, Node 24 are installed.

### Keep it simple (the user is a beginner)

- **Standalone components only** (no NgModules).
- **Plain `@Component` + `.ts` / `.html` / `.scss`** per component; keep each file small.
- Use `HttpClient` + **`subscribe()`** in components. Avoid advanced RxJS (`switchMap`, `combineLatest`, …).
- Use simple class properties (`payments: Payment[] = []`) rather than signals; use `@if` / `@for` in templates.
- Use **template-driven forms** (`[(ngModel)]`) instead of reactive forms.
- Add a short comment above anything non-obvious explaining *why*. No clever generics or abstractions.
- Explain new Angular concepts briefly in the reply when first used.

### Structure (built to grow with the backend)

```
frontend/src/app/
├── models/        # one interface per backend shape: payment.ts, template.ts, bulk-result.ts
├── services/      # ALL HTTP calls live here: payment.service.ts, template.service.ts, auth.service.ts
├── pages/         # one folder per screen: payments-list, payment-form, bulk-upload, templates, login
└── app.routes.ts  # one route per page
```

Why: components never call `HttpClient` directly, so when the backend adds/changes an endpoint, only a
`models/` file and a `services/` file change. A new backend resource = new model + new service + new page.

### Conventions

- API base URL: **relative `/api/...`** only. Never hardcode `localhost:8080`.
- **CORS:** don't touch the backend. Use Angular's dev proxy (`frontend/proxy.conf.json`, `/api` → `http://localhost:8080`)
  and run `ng serve --proxy-config proxy.conf.json` (wire it into `angular.json` so plain `ng serve` works).
- **Docker-ready:** because the frontend only calls relative `/api/...`, it can later be served by nginx in its own
  container (add a `frontend` service to `payments/docker-compose.yaml` or a new compose file) with nginx proxying
  `/api` to the `app` service. Same code, no changes. Only add this when the user asks; it touches compose files.
- **Auth:** a login page collects username/password, `AuthService` stores them in memory/`sessionStorage`,
  and one **HTTP interceptor** adds the `Authorization: Basic ...` header to every request.
  Hide template pages/menu items for users without the `TEMPLATE` role (backend still enforces it).
- **Errors:** backend errors are plain text, so read `error.error` as a string and show it to the user.
  Handle 401 (send to login) and 403 (show "not allowed").
- Field names in models must match the backend JSON exactly (including `requestList` and `TemplateName`).

### Commands

```
cd frontend
npm install
ng serve          # http://localhost:4200 (proxies /api to :8080)
ng build
ng test
```

## MCP servers (`.mcp.json`)

- `angular-cli`: Angular docs, best practices and examples. Use it before writing Angular code.
- `context7`: current docs for any library (Spring Boot 4.1, RxJS, ...). Use it instead of relying on memory for
  recent APIs.
- `playwright`: drives a real browser. After building a page, run it (`ng serve` + backend) and check login,
  forms and uploads yourself; report what you actually saw.
- Skill `add-backend-resource` (`.claude/skills/`): the checklist for supporting a new backend resource
  (model, service, page, route). Use it whenever the backend grows.
- Not added yet: a read-only Postgres server (to inspect saved payments in Docker mode). Add it when needed.
- Never put real credentials in `.mcp.json`; it is meant to be committed.
