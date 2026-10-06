---
name: angular-conventions
description: QuickPay Angular conventions for components, styling and folder layout. Use whenever creating or editing anything under frontend/ (components, pages, services, models, routes, CSS).
---

# Angular conventions (QuickPay)

Full text with examples: `FRONTEND-CONVENTIONS.md` at the repo root. Read it if anything below is unclear.
The user is new to Angular: keep code simple and explain new concepts briefly.

## Components
- **Standalone only.** Never write `@NgModule`. Each component lists its own `imports`.
- **Page components** (`pages/<name>/`) own data and call services. **Presentational components** (`components/<name>/`)
  never inject services; they use `@Input()` / `@Output()` only. Create one only when markup is reused or a page is too long.
- One folder per component with separate `.ts`, `.html`, `.css`. No inline templates/styles.
- Files and folders `kebab-case`, classes `PascalCase`, selector prefix `app-`.
  Pages and components have **no** `.component` suffix (`payments-list.ts`, class `PaymentsList`).
  Services use `.service.ts`; models are plain interfaces.
- No `HttpClient` in components: all HTTP lives in `services/`. Use `subscribe()`, plain class properties
  (no signals), template-driven forms (`[(ngModel)]`), `@if` / `@for`.
- Comment *why* above anything non-obvious.

## Styling
- **Plain CSS**, one `.css` per component. No SCSS, no UI library unless the user asks.
- Colors/spacing/fonts are CSS variables in `src/styles.css` (`var(--color-primary)`); never hardcode them in components.
- `styles.css` is for globals only (variables, body, shared classes like `.error`).

## Directories (`frontend/src/app/`)
- Type-based: `models/`, `services/`, `interceptors/`, `guards/`, `components/` (shared), `pages/` (one folder per screen).
- New screen: `pages/<name>/` + route in `app.routes.ts`.
- New backend resource: `models/<x>.ts` + `services/<x>.service.ts` + page + route.
- Used by 2+ pages: `components/`. Used by one page: inside that page's folder.
- API calls use relative `/api/...` only.

## Checklist before finishing
1. No `NgModule`, no `HttpClient` in a component, no hardcoded colors.
2. File names and locations match the table above.
3. Field names in models match the backend JSON exactly.
