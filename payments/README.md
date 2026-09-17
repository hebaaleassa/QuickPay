# Phase 2 — Clean Architecture Refactor, Domain Validation, Sequence IDs

Builds directly on Phase 1 (`PROJECT-PLAN.md`) — same three capabilities (create / view / list), no
new endpoints. This phase is entirely about *how* the code is structured and *what it guarantees*,
not about adding features.

## Goal

1. Split `payments` into two Maven modules: `domain` and `application`. `domain` must have zero
   Spring/JPA dependency; `application` depends on `domain`, never the other way round.
2. Follow Onion/Clean architecture: business rules belong in `domain`, everything framework-specific
   (persistence, web, wiring) stays in `application`.
3. Keep `PaymentService` — don't delete it. It should end up doing far less than it does today.
4. Move payment validation into `domain`, enforced the moment a `Payment` is created — not scattered
   across the controller, the service, or trusted from the request body.
5. Switch the payment id generation strategy to `SEQUENCE` (replacing whichever of `IDENTITY`/`UUID`
   you're currently on).
6. Add a new field to `Payment`: `creditorName` (see validation table below).

Figure out the how yourselves — you already have the concepts from Day 4's hands-on. If you get
stuck on a specific mechanical problem (a Maven error, a Spring wiring issue), ask; don't ask for the
overall shape of the solution.

## Validation — must live in `Payment`'s own construction, not the DTO

The point: `Payment` must be impossible to construct in an invalid state, no matter which caller
tries — the domain doesn't trust anyone, including your own controller.

| Field | Validation rule | Why |
|---|---|---|
| `senderAccount` | Not null/blank; reasonable max length (e.g. ≤ 34 chars) | Can't identify where a payment came from without it |
| `receiverAccount` | Not null/blank, same length rule, **and must differ from `senderAccount`** | A payment to yourself isn't a real transfer — the first real business rule worth enforcing |
| `amount` | Not null; must be strictly positive (`> 0`); at most 2 decimal places | A zero, negative, or fractional-cent amount is either meaningless or a bug upstream |
| `currency` | Not null/blank; must match `^[A-Z]{3}$` (three uppercase letters) | A cheap, no-lookup-table way to catch obviously-wrong currency input (`"usd"`, `"US Dollar"`, empty) |
| `status` | **Not accepted from outside at all** — always starts as `"PENDING"` | This is the exact mass-assignment risk from Day 4 — a client must never be able to set this |
| `createdAt` | **Not accepted from outside** — always stamped at creation time | Same reasoning as `status` |
| `notes` | Optional; if present, capped at a reasonable max length (e.g. ≤ 500 chars) | Free text still needs a bound, or it becomes an unbounded storage sink |
| `creditorName` | Not null/blank; must be exactly two parts separated by a single space (e.g. a first and last name); each part alphanumeric only; max length 100 overall | A recognizable named party on the payment, without accepting arbitrary/garbage text |

## Done means

- `payments` builds as `domain` + `application` modules, and `domain` has no Spring/JPA dependency —
  prove it, don't just assume it.
- A payment can't be constructed in violation of any rule above, from any code path — including one
  that bypasses the DTO entirely.
- The controller no longer touches persistence types directly.
- Payment ids are generated via `SEQUENCE`.
- All three endpoints (create / view / list) still work end to end.
