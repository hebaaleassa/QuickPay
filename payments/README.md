# Day 10 — Spring Security & Basic Auth

## Goal

Stop anyone from calling every endpoint with no login at all. By the end of today you should be able to
explain, in your own words, why an app needs security in the first place, know the difference between
"who are you" and "are you allowed to do this," understand what Basic Auth actually sends over the wire,
and be able to lock down a real Spring Boot app with usernames, passwords, and roles from nothing.

## Why it matters

Right now, this app will let anyone create a payment, delete a template, read every record — no questions
asked. That's fine for a training exercise; it would be a disaster for anything real. Every production
service has to answer two questions before it does *anything* on a request: is this a real, recognized
caller, and — separately — is this specific caller allowed to do this specific thing. Security isn't a
feature you bolt on at the end; it's the layer that sits in front of everything else you've built and
decides whether your controller code ever gets to run at all.

## Concept walkthrough

### 1. Authentication vs Authorization — the one distinction that matters most

These two words get used interchangeably by beginners, and they mean completely different things:

- **Authentication** answers **"who are you?"** — verifying an identity. Did you prove you're really
  Alice? This happens first.
- **Authorization** answers **"are you allowed to do this?"** — checking permissions for an *already
  verified* identity. Now that I know you're Alice, can Alice do *this specific thing*?

The two failure responses map directly onto this distinction, and you should never mix them up:

- **`401 Unauthorized`** — actually means "I don't know who you are." No credentials, or the credentials
  you sent don't match anyone real.
- **`403 Forbidden`** — means "I know exactly who you are, and the answer is no." You authenticated fine;
  you just don't have permission for this particular action.

If you remember nothing else from today, remember this: **authentication happens once, authorization
happens per-request, per-action.** A single logged-in user can be authorized for some things and denied for
others.

### 2. Why security has to exist at all

Without it, an HTTP API is just a function anyone on the internet can call with any input. Two things
security exists to prevent:

- **Impersonation** — someone acting as if they were a different, legitimate user.
- **Privilege escalation** — a legitimate, correctly-identified user doing something they were never meant
  to do (a regular user deleting another user's data, for example).

Authentication defends against the first. Authorization defends against the second. You need both — an app
that only checks "are you logged in" but not "are you allowed to do *this*" will happily let any logged-in
user do anything.

### 3. What Basic Auth actually is

There are many ways to authenticate (sessions with cookies, OAuth2/OIDC, JWTs). **Basic Auth is the
simplest one that exists**: the caller attaches their username and password to *every single request*,
inside an HTTP header, with no login page, no session, no token issued back.

```
Authorization: Basic YWxpY2U6YWxpY2UxMjM=
```

That base64 blob is nothing but `username:password` encoded (not encrypted — base64 is trivially
reversible, which is exactly why Basic Auth is only considered safe over HTTPS). This is what `curl -u
alice:alice123` and Postman's "Basic Auth" tab are doing for you under the hood — attaching that header on
your behalf.

**Why it's the right starting point for this lesson, and the wrong choice for a lot of real production
systems**: it's dead simple to understand and to test with curl/Postman, which is exactly why we're using it
today. But it means sending your actual password on every request forever, with no way to "log out" short of
changing the password. Real systems built for browsers or long-lived sessions usually use something session-
or token-based instead — but every one of those still boils down to the same two questions: who are you,
and what are you allowed to do.

### 4. Securing this app, step by step

**Step 1 — add the dependency.** The moment `spring-boot-starter-security` is on the classpath, Spring Boot
auto-configures a default security setup that locks down *every* endpoint. Nothing works until you define
your own rules — this is deliberate; a new dependency should never silently leave things open.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Step 2 — define who your users are.** For this exercise, a fake in-memory list is enough — no database
involved:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager(
            User.withUsername("alice")
                    .password(passwordEncoder.encode("alice123"))
                    .roles("PAYMENT")
                    .build(),
            User.withUsername("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles("TEMPLATE")
                    .build()
    );
}
```

Two things worth being precise about:

- **Never store plain-text passwords, even in a demo.** `BCryptPasswordEncoder` hashes the password
  (a one-way scramble you can't reverse) before it's stored — Spring Security compares hashes, never plain
  text.
- **The single most common mistake here**: calling `.password("alice123")` directly, without
  `passwordEncoder.encode(...)`. Spring Security still uses your registered `PasswordEncoder` to *check*
  logins against whatever's stored — so if the stored value isn't actually a valid hash in that encoder's
  format, every single login attempt fails, with no error telling you why. If you ever see "I set the exact
  right password and I still get 401," check this first.

**Step 3 — define the rules.** This is where authorization actually gets configured — which role can do
what:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/payments/**").hasAnyRole("PAYMENT", "TEMPLATE")
                    .requestMatchers("/api/templates/**").hasRole("TEMPLATE")
                    .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

Read this top to bottom, like a list of checkpoints:
- Anything under `/api/payments/` needs the `PAYMENT` role *or* the `TEMPLATE` role.
- Anything under `/api/templates/` needs the `TEMPLATE` role specifically — `PAYMENT` alone isn't enough.
- Anything else at all just needs *some* logged-in user (the safety net for anything not explicitly listed).

`.httpBasic(Customizer.withDefaults())` is the line that actually turns on Basic Auth as the mechanism used
to answer "who are you" for all of the above.

**Why `.csrf(...).disable()` is here**: CSRF protection defends against a browser being tricked by a
different website into submitting a request using your *automatically-attached session cookie*. We have no
sessions or cookies at all — every request carries its own credentials on purpose, every time — so there's
nothing for CSRF to protect, and leaving it on would just block your own legitimate requests from curl and
Postman.

**Step 4 — actually test it, both ways it can fail.**

```bash
# no credentials at all -> 401 (authentication failure)
curl -i http://localhost:8080/api/payments

# alice, wrong password -> 401 (authentication failure)
curl -i -u alice:wrongpassword http://localhost:8080/api/payments

# alice, correct password, reading payments -> 200 (she has PAYMENT)
curl -i -u alice:alice123 http://localhost:8080/api/payments

# alice, correct password, reading templates -> 403 (authorization failure - wrong role)
curl -i -u alice:alice123 http://localhost:8080/api/templates

# admin, correct password, reading templates -> 200 (he has TEMPLATE)
curl -i -u admin:admin123 http://localhost:8080/api/templates
```

If you only ever test the "it works" case, you haven't actually proven your rules do anything — the 401 and
403 cases are the ones that prove the security is real.

### 5. A gotcha specific to testing this with `@WebMvcTest`

`@WebMvcTest` only loads the one controller you're testing — it deliberately does **not** automatically pick
up your other `@Configuration` classes, including your security config. Skip this and your tests fall back
to Spring Boot's own generic default security setup instead of yours, which produces confusing `403`s from a
CSRF check that has nothing to do with your actual role rules. Fix: explicitly import it.

```java
@WebMvcTest(PaymentController.class)
@Import({GlobalExceptionHandler.class, PaymentMapperImpl.class, SecurityConfig.class})
@WithMockUser(roles = "PAYMENT")
class PaymentControllerTest { ... }
```

`@WithMockUser` is the test-only equivalent of "pretend I'm logged in as this role" — there's no real HTTP
request with a password to send in a unit test, so this fakes the authenticated identity directly.

## Core exercises

### Exercise 1 — Secure the app from nothing

Add the dependency, write the config from scratch (don't copy-paste — type it out), and prove it's actually
locked down: confirm every endpoint returns `401` with zero changes beyond adding the dependency and one
`anyRequest().authenticated()` rule.

### Exercise 2 — Hit both failure modes on purpose, and tell them apart

Call a protected endpoint with no credentials (expect `401`), then with valid credentials but the wrong role
for that action (expect `403`). Explain, in your own words, why these are different failures and why an app
should never return the same status code for both.

### Exercise 3 — Reproduce the "forgot to encode" bug on purpose

Set a user's password with `.password("mypassword")` directly, skipping `passwordEncoder.encode(...)`.
Confirm that logging in with the *exact correct* password still fails. Then fix it, and confirm it works.
Explain what was actually being compared to what, and why it always failed silently rather than throwing an
obvious error.

### Exercise 4 — Design two roles for a resource you own

Pick two actions in this app (e.g. "read templates" vs "delete templates") and design two roles where one
user can do only one of them and another user can do both — matching the same pattern as `PAYMENT`/
`TEMPLATE` above. Write the rule, create the two users, and prove the boundary with real requests.

### Exercise 5 — Fix the `@WebMvcTest` trap

Take an existing `@WebMvcTest` for a controller with no security-aware tests yet. Add security to the app,
watch the existing tests break with a `403` that has nothing to do with your actual rules, diagnose why
(hint: what config is and isn't loaded by `@WebMvcTest`), and fix it with `@Import` + `@WithMockUser`.

## Done checklist

- [x] I can explain the difference between authentication and authorization in one sentence each, without
  using the word "auth" to define either one.
- [x] I know which HTTP status code means "I don't know who you are" and which means "I know who you are
  and the answer is no," and why they must never be the same code.
- [x] I can explain what Basic Auth actually sends on the wire, and why it's base64 (encoded) rather than
  encrypted.
- [x] I secured this app from an empty `SecurityConfig`, with in-memory users, two roles, and real rules.
- [x] I hit `401` and `403` on purpose, and can explain exactly why each one happened.
- [x] I reproduced the "forgot to call `passwordEncoder.encode(...)`" bug on purpose, and can explain why it
  fails silently instead of throwing an obvious error.
- [x] I fixed a `@WebMvcTest` that broke when security was added, and can explain why `@WebMvcTest` didn't
  pick up my security config automatically.
- [x] I can explain, in my own words, why `.csrf().disable()` is correct for this specific API and would
  not be correct for a browser-based, cookie-session app.

## Suggested resources

- Spring Security reference, "Authentication" — https://docs.spring.io/spring-security/reference/servlet/authentication/index.html
- Spring Security reference, "Authorization" — https://docs.spring.io/spring-security/reference/servlet/authorization/index.html
- Spring Security reference, "Basic Authentication" — https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html
- Spring Security reference, "Testing" (`@WithMockUser`) — https://docs.spring.io/spring-security/reference/servlet/test/method.html
- OWASP, "Authentication vs. Authorization" — https://owasp.org/www-community/Broken_Authentication
