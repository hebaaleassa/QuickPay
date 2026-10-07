# How login, access tokens and refresh tokens work in QuickPay

## The two tokens

| | Access token | Refresh token |
|---|---|---|
| Lifetime | 5 minutes (`jwt.expiry-minutes`) | 60 minutes (`jwt.refresh-expiry-minutes`) |
| Claims | `sub`, `roles`, `ver`, `type=access` | `sub`, `ver`, `type=refresh` |
| Sent to | every `/api/...` call, as `Authorization: Bearer <token>` | only `/api/auth/refresh` |
| Stored in | `sessionStorage` (`token`) | `sessionStorage` (`refreshToken`) |

Both are JWTs signed with the same key (HS256). The `type` claim keeps them apart: each decoder
rejects the other type, so a refresh token cannot be used as an access token and the reverse.

## Login

1. The user enters username and password on the login page.
2. `POST /api/auth/login` checks them (BCrypt).
3. The backend returns `{ token, refreshToken }`.
4. `AuthService` saves both in `sessionStorage`.

## Normal use and automatic renewal

1. The interceptor adds `Authorization: Bearer <access token>` to every request
   (except login and refresh).
2. After 5 minutes the access token expires and the backend answers **401**.
3. The interceptor calls `AuthService.refresh()` -> `POST /api/auth/refresh` with the refresh token.
4. The backend returns a **new pair** (new access token AND new refresh token).
5. The interceptor sends the failed request again, once, with the new access token.
   The user notices nothing.
6. If several requests get a 401 at the same moment, `shareReplay` makes them share
   one refresh call.

Each refresh gives a new refresh token with a fresh 60 minutes. So the user is only forced to
log in again after **60 minutes without a refresh** (for example an idle tab).
If the refresh itself fails (401), the interceptor logs out and goes to `/login`.

## Expiry vs. version: two separate checks

The backend checks both on every token:

| Check | What it is | What changes it |
|---|---|---|
| **Expiry** (`exp`) | A clock. The token dies after 5 or 60 minutes. | Time passing. Every refresh starts a new clock. |
| **Version** (`ver`) | A number copied from `TokenVersionStore` when the token is created. | **Only** `logout-all` or an admin revoke (each adds 1). |

- Refreshing does **not** change `ver`.
- The access token and the refresh token carry the **same** `ver` (one counter per user).
- The counter is per user: revoking `alice` does not affect `admin`.
- A token is valid only if it has not expired **and** its `ver` equals the user's current `ver`.

### Example

1. Login: user's `ver` is 1. Both tokens are created with `ver=1`.
2. Refresh after 5 minutes: new tokens, still `ver=1`. Everything stays valid.
3. Admin calls `POST /api/auth/revoke/alice`: the store now says `ver=2`.
   Every old token of alice (access and refresh) is rejected. Her next request gets 401,
   the refresh gets 401 too, and she is sent to `/login`.
4. She logs in again: new tokens are created with `ver=2`.

## Logout

| Action | What happens |
|---|---|
| **Simple logout** (`AuthService.logout()`) | Deletes both tokens from `sessionStorage`. **No request to the backend**, `ver` does not change. |
| **Logout all devices** (`POST /api/auth/logout-all`) | Needs a valid access token. Adds 1 to the caller's `ver`, so all their tokens stop working. (`AuthService.logoutAllDevices()`, no page uses it yet.) |
| **Admin revoke** (`POST /api/auth/revoke/{username}`) | ADMIN only. Adds 1 to that user's `ver`. 204, or 404 for an unknown user. |

Closing the tab also clears `sessionStorage`, so it behaves like a simple logout.

### Important: simple logout does not invalidate tokens

After a simple logout the browser forgets the tokens, but the tokens themselves are still valid:

- A copied **access token** works until its 5 minutes run out.
- A copied **refresh token** works until its 60 minutes run out, and it can keep minting new
  pairs (each refresh gives a fresh 60 minutes, and `ver` never changes). So a copied refresh
  token can keep a session alive as long as it is refreshed within 60 minutes of the last time.

Only `logout-all` or an admin revoke makes tokens truly invalid.

## Known limits

- **Old refresh tokens stay valid after a refresh.** There is no one-time use. This would need the
  server to remember used tokens.
- **`ver` is in memory** (`ConcurrentHashMap`). A backend restart resets every user to version 1,
  so tokens revoked before the restart may be accepted again.
- **Roles in the access token** can be up to 5 minutes old. They are re-read from the user on
  each refresh.

## Where the code is

| What | File |
|---|---|
| Create tokens | `payments/application/.../config/JwtService.java` |
| Decoders, type + version checks, access rules | `payments/application/.../config/SecurityConfig.java` |
| `/api/auth/*` endpoints | `payments/application/.../contoller/AuthController.java` |
| Version counter | `payments/application/.../config/TokenVersionStore.java` |
| Token storage, `refresh()`, `logout()` | `frontend/src/app/services/auth.service.ts` |
| Bearer header + refresh-and-retry on 401 | `frontend/src/app/interceptors/auth.interceptor.ts` |
| Route protection | `frontend/src/app/guards/auth.guard.ts` |
