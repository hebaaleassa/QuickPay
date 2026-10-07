# JWT, Token Versioning and Revoke - Simple Guide

This file explains what we built, in simple English.

---

## 1. The problem: how does the server know who you are?

Before, the app used **HTTP Basic**. The client sent the username and password
on **every** request. This is not good, because the password travels all the time.

Now the app uses **JWT**.

---

## 2. What is a JWT?

JWT means **JSON Web Token**. It is a small piece of text. Think of it like a **cinema ticket**:

1. You show your ID **one time** at the desk (this is **login**).
2. The desk gives you a **ticket** (this is the **token**).
3. After that, you only show the ticket. You do not show your ID again.

A JWT looks like this:

```
eyJhbGciOiJIUzI1NiJ9 . eyJzdWIiOiJhbGljZSIs... . muRj_E_E9MGy...
      HEADER                   PAYLOAD               SIGNATURE
```

It has 3 parts, separated by dots:

| Part | What is inside |
|------|----------------|
| **Header** | The type of signing (we use `HS256`). |
| **Payload** | The information (called "claims"). Anyone can read it. |
| **Signature** | A secret stamp. It proves the server made this token. |

Our payload looks like this:

```json
{ "sub": "alice", "ver": 1, "roles": ["PAYMENT"], "iat": 1791233069, "exp": 1791236669 }
```

| Claim | Meaning |
|-------|---------|
| `sub` | Who the user is ("subject"). |
| `ver` | The **version** of the token (we explain this below). |
| `roles` | What the user is allowed to do. |
| `iat` | When the token was made ("issued at"). |
| `exp` | When the token stops working ("expires"). We use 60 minutes. |

### Important things

- **The payload is NOT secret.** Anyone can read it. Never put a password in it.
- **The signature stops cheating.** If someone changes the payload (for example, changes
  `PAYMENT` to `ADMIN`), the signature does not match any more, and the server says no.
- Only the server knows the **secret key** that makes the signature.

---

## 3. The weak point of JWT

The server does not remember tokens. So it cannot cancel one.

Example: Alice's token works for 60 minutes. After 5 minutes you find out the token was
stolen. The token still works for 55 more minutes. We cannot take it back.

This is why we need **token versioning** and **revoke**.

---

## 4. Token versioning (the solution)

The server keeps **one number for each user**. This number is the user's **version**.
It is saved in memory (a small map), not in the database.

```
alice -> 1
admin -> 1
```

The same number is also written **inside the token** (the `ver` claim).

On **every request**, the server checks:

```
Is the version in the token equal to the user's version on the server?
   YES -> let the request pass
   NO  -> reject with 401
```

### Example

| Step | What happens | Server version for alice | `ver` in token | Result |
|------|--------------|:------------------------:|:--------------:|--------|
| 1 | Alice logs in, gets token A | 1 | 1 | OK |
| 2 | Alice uses token A | 1 | 1 | **200 OK** |
| 3 | The version goes **up** (logout-all or revoke) | **2** | 1 | |
| 4 | Someone uses token A | 2 | 1 | **401 rejected** |
| 5 | Alice logs in again, gets token B | 2 | 2 | **200 OK** |

When we increase the number, **all old tokens of that user die at once**.
We do not need to remember every token. We only remember one number.

---

## 5. Revoke

**Revoke** means: "cancel the tokens of a user before they expire."

Revoke = **increase the user's version by 1**. That is all.

We have two ways to do it:

| Endpoint | Who can use it | What it does |
|----------|----------------|--------------|
| `POST /api/auth/logout-all` | Any logged-in user | Cancels **your own** old tokens. |
| `POST /api/auth/revoke/{username}` | Only users with role `ADMIN` | Cancels the tokens of **another user**. |

Example: Alice's token was stolen. The admin calls `POST /api/auth/revoke/alice`.
Alice's version goes from 1 to 2. The stolen token (`ver: 1`) does not work any more.

Revoke answers:

| Code | Meaning |
|------|---------|
| `204` | Done. |
| `403` | You are not an ADMIN. |
| `404` | This user does not exist. |
| `401` | You are not logged in. |

---

## 6. The full flow, step by step

### A) Login

`POST /api/auth/login` with `{"username":"alice","password":"alice123"}`

1. `SecurityConfig` lets this path pass without a token (`permitAll`).
2. `AuthController.login` finds the user and checks the password.
   Wrong user or password gives **401**.
3. `JwtService.createToken` builds the token: username, roles, expiry time, and version.
4. `TokenVersionStore.getVersion` gives the version that goes into the token.
5. `JwtEncoder` (in `SecurityConfig`) signs the token with the secret key.
6. The server returns `{"token": "eyJ..."}`.

### B) A normal request

`GET /api/payments` with the header `Authorization: Bearer <token>`

1. Spring Security takes the token from the header. No token gives **401**.
2. `JwtDecoder` checks the **signature** and the **expiry time**. If bad, **401**.
3. Our `versionCheck` checks the **version** (`TokenVersionStore.isCurrent`). If old, **401**.
4. `jwtAuthenticationConverter` reads `roles` and makes `ROLE_PAYMENT`.
5. The rules in `securityFilterChain` check if the role is allowed. If not, **403**.
6. Only now the real controller (for example `PaymentController`) runs.

### C) Revoke

1. The admin sends `POST /api/auth/revoke/alice` with the admin token.
2. The request passes steps B1 to B4. Then the rule says: only `ADMIN` is allowed.
3. `AuthController.revoke` increases alice's version.
4. Alice's old token now fails at step B3.

---

## 7. Users and roles

| User | Password | Roles | Can do |
|------|----------|-------|--------|
| `alice` | `alice123` | `PAYMENT` | Use `/api/payments/**` |
| `admin` | `admin123` | `TEMPLATE`, `ADMIN` | Use payments, templates, and revoke other users |

---

## 8. What every file does

### `pom.xml` (changed)
Added one library: `spring-boot-starter-security-oauth2-resource-server`.
It gives us the tools to create and check JWTs.

### `application.properties` (changed)
```properties
jwt.secret=${JWT_SECRET:quickpay-dev-secret-change-me-0123456789}
jwt.expiry-minutes=60
```
- `jwt.secret` is the secret key. It must be **at least 32 characters**.
  It takes the value from the environment variable `JWT_SECRET`.
  If that variable is missing, it uses the default after the `:`.
  **In a real system, always set your own `JWT_SECRET`.**
- `jwt.expiry-minutes` is how long a token lives (60 minutes).

### `TokenVersionStore.java` (new) - the memory
Keeps the version of each user in a map.

| Method | What it does |
|--------|--------------|
| `getVersion(username)` | Gives the user's version. A user who is not in the map has version **1**. |
| `increment(username)` | Adds 1 to the user's version. This is the "revoke". |
| `isCurrent(username, versionInToken)` | Gives `true` only if the token's version **equals** the user's version. |

Note: it is in memory. When the app restarts, all versions go back to 1.

### `JwtService.java` (new) - makes tokens
Method `createToken(user)`:
1. Takes the user's roles (`ROLE_PAYMENT` becomes `PAYMENT`).
2. Writes the claims: `sub`, `iat`, `exp`, `roles`, and `ver`.
3. Signs everything with `HS256` and returns the token as text.

### `AuthController.java` (new) - the public doors
| Method | Endpoint | What it does |
|--------|----------|--------------|
| `login` | `POST /api/auth/login` | Checks the password and gives a token. |
| `logoutAll` | `POST /api/auth/logout-all` | Increases **your own** version. |
| `revoke` | `POST /api/auth/revoke/{username}` | Increases **another user's** version (admin only). Gives 404 if the user does not exist. |

### `SecurityConfig.java` (changed) - the gatekeeper
This is the most important file. Each part:

| Part | What it does |
|------|--------------|
| `passwordEncoder` | Hashes passwords with BCrypt. |
| `userDetailsService` | The list of users (alice and admin). |
| `tokenVersionStore` | Creates the one shared version memory. |
| `jwtSecretKey` | Makes the secret key from `jwt.secret`. |
| `jwtEncoder` | Signs new tokens with the key. |
| `jwtDecoder` | Checks incoming tokens: signature, expiry, **and our version check**. |
| `jwtAuthenticationConverter` | Reads the `roles` claim and adds the `ROLE_` prefix, so `hasRole(...)` works. |
| `securityFilterChain` | The rules (see below). |

The rules in `securityFilterChain`, from top to bottom (the **first match wins**):

```java
GET  /, /index.html, /favicon.ico, /*.js, /*.css  -> everyone (the Angular page)
POST /api/auth/login                               -> everyone
POST /api/auth/revoke/*                            -> only ADMIN
/api/payments/**                                   -> PAYMENT or TEMPLATE
/api/templates/**                                  -> TEMPLATE
everything else                                    -> must be logged in
```

Other settings there:
- `csrf disabled` - we do not use cookies, so we do not need it.
- `STATELESS` - the server keeps **no session**. The token is the only proof.
- `oauth2ResourceServer(...jwt...)` - turns on the JWT checking.

### Other changes in this task

| File | Change | Why |
|------|--------|-----|
| `Dockerfile` (root) | Uses `COPY --from=m2local ...` instead of building `file-parser` | The library `file-parser-core` is not on the internet. We copy the built copy from `~/.m2` on your computer. |
| `docker-compose.prod.yaml` | `additional_contexts: m2local: ${HOME}/.m2/repository/com/progressoft/training` | Gives Docker the folder for `m2local`. |
| `SecurityConfig` (static files rule) | Allow the Angular files without a token | A browser cannot send a token when it loads a page. Without this, the page gives 401. |

### Tests (new)
- `TokenVersionStoreTest` - tests the version map (4 tests).
- `AuthControllerTest` - tests revoke: admin can (204), unknown user (404),
  normal user cannot (403), not logged in (401).

---

## 9. How to test with Postman

1. **Login:** `POST http://localhost:8080/api/auth/login`
   Body (raw, JSON): `{"username":"alice","password":"alice123"}`
   Copy the `token` from the answer.
2. **Use the token:** `GET http://localhost:8080/api/payments`
   Tab **Authorization** -> Type **Bearer Token** -> paste the token. Expect `200`.
3. **Role test:** `GET /api/templates` with alice's token. Expect `403`.
4. **Revoke test:**
   - Login as `admin` / `admin123` and copy the admin token.
   - `POST /api/auth/revoke/alice` with the **admin** token. Expect `204`.
   - `GET /api/payments` with alice's **old** token. Expect `401`.
   - Login as alice again. The new token works.

(If you run the app on a different port, use that port instead of 8080.)

---

## 10. Limits you should know

1. **Memory only.** The versions are in memory. If the app restarts, they go back to 1,
   and a revoked token can work again. The fix is to save versions in a database (later).
2. **Secret key.** The default `jwt.secret` is only for development. Set `JWT_SECRET` for real use.
3. **Old admin tokens.** An admin token made before the `ADMIN` role was added does not have
   the role. The admin must log in again.
4. **The Angular app is not ready yet.** It still needs a login page that calls
   `/api/auth/login`, saves the token, and sends `Authorization: Bearer <token>`.
   Refreshing a page on an Angular route (like `/payments`) also needs a backend fix later.

---

## 11. Short summary (5 lines)

1. Login gives a **token** (a signed ticket with name, roles, expiry, and version).
2. Every request sends the token. The server checks the **signature, expiry, and version**.
3. The server only remembers **one number per user** (the version).
4. **Revoke** = make that number bigger. All old tokens of that user stop working.
5. Users can revoke themselves (`logout-all`). Admins can revoke anyone (`revoke/{username}`).
