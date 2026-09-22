# Day 7 — Spring Profiles & Environment-Specific Configuration

## Goal

Learn how to make one codebase, one jar, behave differently depending on where it runs — different
config values, and even different bean implementations — without an `if` statement anywhere and
without a rebuild. That means: multiple properties files and how they layer, `@Profile` for swapping
which bean gets registered, `@Primary` for the different problem of breaking a tie between
candidates, and Liquibase's own equivalent of the same idea — `context` — so your schema migrations
can be environment-aware too.

## Why it matters

Every real service runs in more than one place: your machine, CI, staging, a specific customer's
environment. Each of those needs slightly different behavior — a different database URL, a stub
integration instead of a real one, seed data that should only ever exist in dev. Hard-coding any of
that, or branching on an environment variable inside your business logic, doesn't scale past the
second environment. Profiles are Spring's answer: pick the environment at *startup*, and let
Spring/Liquibase wire in whatever that environment needs.

## Concept walkthrough

### 1. Multiple properties files — the layering, with sample code

`application.properties` always loads. A file named `application-<profile>.properties` loads
*in addition*, only when that profile is active — and where both define the same key, **the
profile-specific value wins**.

```properties
# application.properties — the defaults, always applied
spring.application.name=payments
payments.default-currency=USD
payments.bulk-max-rows=1000
```

```properties
# application-customerA.properties — only applied when "customerA" is active
payments.default-currency=JOD
```

```properties
# application-customerB.properties — only applied when "customerB" is active
payments.default-currency=EUR
```

Activate one at startup:

```bash
java -jar app.jar --spring.profiles.active=customerA
# or, during development:
mvn spring-boot:run -Dspring-boot.run.profiles=customerA
```

With no profile passed at all, Spring Boot logs `No active profile set, falling back to 1 default
profile: "default"` — only the base file applies, `payments.default-currency` stays `USD`. Reading
the resolved value back out is the same `@Value` you've already seen:

```java
@Service
public class PaymentService {
    @Value("${payments.default-currency}")
    private String defaultCurrency;
}
```

The property doesn't know or care which file it came from by the time `@Value` resolves it — that's
the whole point: your code stays identical, only the active profile changes what gets injected.

### 2. `@Profile` — swapping which bean gets registered

Properties files change *values*. `@Profile` changes something bigger: **which class Spring
instantiates at all**. Put it on a `@Component`/`@Service`/`@Bean` and that bean is only ever created
when a matching profile is active — non-matching ones aren't just unused, they're never constructed:

```java
public interface NotificationSender {
    void send(String message);
}

@Component
@Profile("dev")
public class ConsoleNotificationSender implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println("[DEV] " + message);
    }
}

@Component
@Profile("prod")
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String message) {
        // real email/SMS integration lives here
    }
}
```

Anything that just injects `NotificationSender` (a constructor parameter, a `@RequiredArgsConstructor`
field) never has to know which one it got — that's decided entirely by which profile started the app.

A few things worth being precise about:

- **Combine profiles**: `@Profile({"dev", "test"})` is an OR (active if either is active).
  `@Profile("!prod")` is a negation (active whenever `prod` is *not* active) — a common way to write
  "everything except production" once, instead of listing every non-prod profile by name.
- **The gotcha you will hit**: if *no* active profile matches any `@Profile` on a given interface's
  implementations, **no bean gets created at all** — not a default, not a no-op, nothing. Injecting
  `NotificationSender` then fails at startup with `NoSuchBeanDefinitionException`, not a runtime
  surprise later. This is `@Profile`'s version of a compile error: it fails loud, at boot, which is
  exactly what you want instead of silently running with nothing wired in.
- **`@Profile("default")`** is special — it means "active only when *no* profile was explicitly set,"
  i.e. exactly the fallback case above. That's a legitimate way to give an interface a genuine default
  implementation for local/no-profile runs, while `dev`/`prod`/`customerA` etc. each override it.

### 3. `@Primary` — a different problem: breaking a tie, not choosing by environment

`@Profile` decides *whether* a bean exists for the current environment. `@Primary` solves a completely
different problem: **more than one bean of the same type is already active at once**, and a plain
injection point (no `@Qualifier`) needs Spring to pick one without throwing
`NoUniqueBeanDefinitionException`.

This comes up more than it sounds like it should — two profiles active together
(`spring.profiles.active=dev,test`), or two implementations that were never profile-gated in the
first place:

```java
public interface FeeCalculator {
    BigDecimal calculate(BigDecimal amount);
}

@Component
@Primary
public class StandardFeeCalculator implements FeeCalculator { /* ... */ }

@Component
public class PromotionalFeeCalculator implements FeeCalculator { /* ... */ }
```

Anywhere that just injects `FeeCalculator feeCalculator` gets `StandardFeeCalculator` — `@Primary`
is the tie-breaker default. The one place that specifically needs the other one asks for it by name:

```java
public PaymentService(@Qualifier("promotionalFeeCalculator") FeeCalculator feeCalculator) { ... }
```

**When to actually reach for `@Primary` vs. `@Profile`**: if only one implementation should ever
exist for a given environment, that's `@Profile` — there's no ambiguity to break, because only one
candidate is ever in the context. Reach for `@Primary` when multiple implementations are
*legitimately* active at the same time and you want a sensible default for most callers, with
specific callers opting into the non-default one via `@Qualifier`. Using `@Primary` to paper over
what should really be `@Profile` (e.g. marking the "prod" bean `@Primary` instead of profile-gating
the others) just means all the other implementations are silently sitting in the context too,
findable by anyone with a `@Qualifier` — not what you want for something like a fake vs. real payment
gateway.

### 4. Liquibase `context` — the same idea, for migrations

Spring beans aren't the only thing that should differ per environment — schema changes can too: seed
data that should only exist in dev, or a changeset that only makes sense before a customer-specific
migration. Liquibase's `context` attribute on a `<changeSet>` is its own, independent version of
exactly the same concept as `@Profile`:

```xml
<changeSet id="3" author="jane">
    <createTable tableName="templates">
        <!-- runs everywhere, no context specified -->
    </createTable>
</changeSet>

<changeSet id="4" author="jane" context="dev">
    <insert tableName="templates">
        <column name="name" value="sample-template"/>
    </insert>
</changeSet>
```

A changeset with no `context` always runs. One with a `context` only runs when that context is
active for the current execution — controlled by a property, most naturally tied straight to the
active Spring profile:

```properties
spring.liquibase.contexts=${spring.profiles.active}
```

With that in place, starting the app under `dev` runs changeset 4 (seed data) as well as changeset 3;
starting it under `prod` (or with no profile at all) skips changeset 4 entirely — same database
migration history, no seed rows ever touching a real environment. Context expressions support the
same combinators you just saw on `@Profile`: `context="dev or test"`, `context="!prod"`.

The parallel worth holding onto: `@Profile` decides which *beans* exist for this run; Liquibase
`context` decides which *changesets* run for this run. Two different subsystems, the same underlying
question — "what environment am I in right now" — answered once (the active profile) and threaded
through both.

## Core exercises

### Exercise 1 — Two implementations, one interface, gated by `@Profile`

Create a small interface with two implementations (like `NotificationSender` above), gate them with
`@Profile`, inject the interface somewhere it gets called, and run your app under each profile in
turn. Prove — by observing actual behavior (a log line, a response), not by reading your own code —
that a different implementation ran each time.

### Exercise 2 — Hit the "no matching bean" error on purpose

Start your app with a profile active that matches *neither* of your two `@Profile` values from
Exercise 1 (e.g. `-Dspring-boot.run.profiles=staging`). Read the actual `NoSuchBeanDefinitionException`
Spring gives you. Then fix it by adding an `@Profile("default")` implementation, and confirm running
with *no* profile at all now uses it.

### Exercise 3 — Hit the ambiguity error on purpose, then fix it with `@Primary`

Temporarily remove the `@Profile` annotations from Exercise 1's two beans so both are active
simultaneously. Confirm you get `NoUniqueBeanDefinitionException` at startup. Fix it by adding
`@Primary` to one of them. Then add a second, explicit injection point that uses
`@Qualifier(...)` to deliberately get the *other* (non-primary) one, and confirm both injection
points really did get different beans.

### Exercise 4 — A real setting, driven by profile

Pick one real value in your project (a default currency, a fee, a limit). Move it into
`application.properties` with a sensible default, then add two `application-<name>.properties` files
overriding it differently. Run under each and confirm — via your running application, not by reading
the file — that the value actually changed.

### Exercise 5 — Liquibase context

Add a new changeset with a `context` attribute (seed data is a good candidate). Set
`spring.liquibase.contexts=${spring.profiles.active}`. Run your app under a profile that matches the
context and confirm (H2 console, or a `GET` that lists the seeded row) that it ran. Run it again under
a different profile and confirm it didn't.

## Stretch exercises (if you finish early)

- Write a context expression combining two contexts (`context="dev or test"`) and confirm it runs
  under either.
- Write a `@Profile("!prod")` bean and confirm it's active under `dev`, under no profile at all, and
  inactive only under `prod`.
- Activate two profiles at once (`-Dspring-boot.run.profiles=dev,test`) and check which
  profile-specific properties file "wins" when both define the same key — read the Spring Boot docs
  on activation order rather than guessing.

## Done checklist

- [x] I can explain how a value in `application-<profile>.properties` overrides the same key in
  `application.properties`, and only when that profile is active.
- [x] I can explain what `@Profile` actually controls (whether a bean is created at all) versus what
  a properties file controls (a value inside a bean that already exists).
- [x] I hit `NoSuchBeanDefinitionException` from an unmatched profile on purpose, and can explain why
  it happens instead of some default silently being used.
- [x] I can explain what `@Profile("default")` specifically means, and when it does or doesn't apply.
- [x] I hit `NoUniqueBeanDefinitionException` from two active, ungated beans on purpose, and fixed it
  with `@Primary`.
- [x] I can explain, in my own words, when a problem calls for `@Profile` versus when it calls for
  `@Primary` — they solve different problems even though both involve "more than one bean".
- [x] I can explain what `@Qualifier` does at an injection point, and why it can still reach a
  non-`@Primary` bean.
- [x] I can explain what a Liquibase `context` is, and that it's answering the same "which
  environment" question as `@Profile` — just for changesets instead of beans.
- [x] I wired `spring.liquibase.contexts` to my active Spring profile and proved a context-gated
  changeset only runs when its context is active.

## Suggested resources

- Spring Boot reference, "Profiles" — https://docs.spring.io/spring-boot/reference/features/profiles.html
- Spring Framework reference, "Bean Definition Profiles" (`@Profile`) — https://docs.spring.io/spring-framework/reference/core/beans/environment.html
- Baeldung, "Spring @Primary Annotation" — https://www.baeldung.com/spring-primary
- Liquibase documentation, "Contexts" — https://docs.liquibase.com/concepts/changelogs/attributes/contexts.html
