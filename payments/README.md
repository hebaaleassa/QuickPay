# QuickPay

Built up in phases. Each phase is scoped narrowly on purpose: get one thing working end to end
before adding the next concern on top.

### Project structure

A single parent project holds everything — a root `pom.xml` with `<packaging>pom</packaging>` and
a `<modules>` list, purely to organize and build things together. Each individual project (starting
with `payments`) keeps its own `spring-boot-starter-parent` — the root pom doesn't replace that, it
just aggregates.

---

## Phase 1 — Payment Service: Basic Setup

### Goal

Stand up `payments` with a minimal, working set of endpoints backed by a real H2 database. No
validation, no business rules yet — just the foundation: a working API, talking to a real database,
nothing more.

### Endpoints

| Operation | Method | URL |
|---|---|---|
| Create a payment | `POST` | `/api/payments` |
| View one payment | `GET` | `/api/payments/{id}` |
| List all payments | `GET` | `/api/payments` |

### Database

H2, via Spring Data JPA. In-memory — no need to survive a restart for this phase.

### `Payment` entity

`id` plus these fields:

| Field | Type | Why |
|---|---|---|
| `senderAccount` | `String` | Who the payment is from |
| `receiverAccount` | `String` | Who the payment is to |
| `amount` | `BigDecimal` | The payment amount — never `double`/`float` for money |
| `currency` | `String` | e.g. `"USD"`, `"EUR"` |
| `status` | `String` | e.g. `"PENDING"`, `"COMPLETED"` |
| `createdAt` | `Instant` | When the payment was created |
| `notes` | `String` | Free-text note about the payment |

### Explicitly out of scope for this phase

- Input validation
- Business rules / status transitions
- Security
- Anything beyond create / view / list

### Done means

- `payments` builds and runs.
- All three endpoints work against a real H2 (in-memory) database — confirmed by creating a
  payment and reading it back via both `GET` endpoints.
- `Payment` has exactly the six fields above, no more.

---

## Later phases

Not defined yet — to be added here once Phase 1 is solid.
