# Frontend Conventions (Angular)

## 1. Component convention

### 1.1 Standalone components only (no NgModule)

Every component declares what it needs in its own `imports` array.

```ts
// pages/payments-list/payments-list.ts
@Component({
  selector: 'app-payments-list',
  imports: [FormsModule],          // only what THIS template uses
  templateUrl: './payments-list.html',
  styleUrl: './payments-list.scss',
})
export class PaymentsList { }
```

We do **not** write `@NgModule` / `declarations` anywhere.

**Why:** less boilerplate, it is the Angular default, and a beginner reads one file instead of two.

### 1.2 Page components first, presentational components only when reused

| Kind | Where | Talks to services? | Example |
|------|-------|--------------------|---------|
| **Page** (one per route) | `pages/<name>/` | Yes | `PaymentsList` loads payments through `PaymentService` |
| **Presentational** (optional) | `components/<name>/` | **No**: only `@Input()` / `@Output()` | `PaymentRow` shows one payment |

Rules:
- A page owns the data, the loading/error state and the service calls.
- A presentational component never injects a service. It receives data and emits events.
- Do **not** create a presentational component "just in case". Extract one only when the same markup is used in
  two places, or a page template gets too long to read.

### 1.3 Naming and files

- One component = one folder with `.ts`, `.html`, `.scss` (no inline templates or styles).
- Folder and file names: `kebab-case` (`payment-form/payment-form.ts`).
- Class names: `PascalCase` (`PaymentForm`).
- Selector prefix: `app-`.
- Pages do not use the `.component` suffix, the same as the Angular CLI default.
- Services use the `.service` suffix (`payment.service.ts`) and models are plain `interface` files (`payment.ts`).

### 1.4 Component rules

- Components never use `HttpClient`. All HTTP calls live in `services/`.
- Plain class properties (`payments: Payment[] = []`), not signals.
- Template-driven forms (`[(ngModel)]`). Template control flow is `@if` / `@for`.
- Keep each file small. If a page grows past about 150 lines of `.ts`, split it.
- Add a short comment above anything non-obvious saying *why*.

## 2. Style convention

**SCSS, no UI library.**

- Each component has its own `.scss` file. Angular scopes it to that component, so class names cannot clash.
- Colors, spacing and fonts live as CSS variables in `src/styles.scss`, so the look changes in one place:
  ```scss
  :root { --color-primary: #1565c0; --space: 8px; }
  ```
  ```scss
  /* in a component .scss */
  button { background: var(--color-primary); padding: var(--space); }
  ```
- `styles.scss` holds only global things: variables, base `body` styles, and a few shared classes (`.error`, `.btn`).
- No Angular Material / Bootstrap for now. Revisit only if the user asks.

**Why:** the CLI project is set up with SCSS, so there is nothing to configure. SCSS is a superset of CSS, so plain CSS (and CSS variables) still works in it. A UI library adds a second thing to learn on top of Angular.

## 3. Directory convention

Type-based at the top level, with **one folder per screen** inside `pages/`.

```
frontend/src/app/
├── models/        payment.ts, template.ts, bulk-result.ts     (one interface per backend shape)
├── services/      payment.service.ts, auth.service.ts, ...    (ALL HTTP calls)
├── interceptors/  auth.interceptor.ts
├── guards/        auth.guard.ts, template-role.guard.ts
├── components/    shared, reusable, presentational pieces (e.g. payment-row/)
├── pages/         one folder per routed screen (payments-list/, payment-form/, login/, ...)
└── app.routes.ts  one route per page
```

Where does a new thing go?

| New thing | Location |
|-----------|----------|
| New screen | `pages/<name>/` + a route in `app.routes.ts` |
| New backend resource | `models/<x>.ts` + `services/<x>.service.ts` + a page |
| Used by 2+ pages | `components/<name>/` |
| Used by only one page | in that page's folder (`pages/<name>/<child>/`) |
| Guard, interceptor | `guards/`, `interceptors/` |

**Why not feature-based folders** (`payments/` holding its own model, service and page)? They suit big apps. Here there are
about five screens, and the type-based layout matches the backend-growth rule in `CLAUDE.md`: a new backend resource means
one new model, one new service, one new page.
