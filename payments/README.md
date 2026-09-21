# Phase 3 — Day 5: Bulk File Import & Template Management

Builds directly on Phase 2 (`PROJECT-PLAN-PHASE-2.md`) — same `payments` app, same clean-architecture
rules from that phase still apply: `domain` has zero Spring/JPA dependency, validation lives in
`domain` and is enforced the moment a `Payment` is created, and the controller never touches
persistence types directly. None of that goes away just because a new feature is landing on top.

## Context

You previously built a small, separate file-parsing library (`file-parser-core`) that already
knows how to: read a delimited file, validate each row against a named "template" (a set of expected
fields — name, length, required or optional), and hand you back the rows that passed along with the
ones that failed and why. It also already ships its own use cases for managing templates (create /
get / update / delete / list). You are not writing any of that parsing or validation logic
yourselves — only figuring out how to wire it into this app correctly, and where the pieces belong.

## Goal — today's flow, in this order

### 1. Bulk transfer upload, against one fixed default template

#### Concept: multipart requests (uploading a file over HTTP)

A file doesn't travel in a JSON body — it needs a `multipart/form-data` request, where the body is
split into named parts (one part can be a file, another can be plain text or JSON). Spring MVC binds
each part to a controller parameter with `@RequestPart`:

```java
@PostMapping(value = "/api/payments/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> uploadBulk(@RequestPart("file") MultipartFile file) throws IOException {
    Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
    file.transferTo(tempFile);
    // hand tempFile to the file-parser library from here
}
```

`MultipartFile` gives you `getOriginalFilename()`, `getInputStream()`, and `transferTo(Path)` — the
library needs an actual file `Path` to parse, so writing the upload to a temp file (and deleting it
once you're done) is the normal pattern here.

One thing to watch for once you get to step 3, where the request also needs to carry a template
name alongside the file: if you bind that second part straight to your own request DTO —
`@RequestPart(...) YourDto metadata` — it'll work fine from `curl` with an explicit
`type=application/json` on that part, but tools like Postman send form-data text fields as
`text/plain` by default, with no easy way to change it. Spring can't find a converter from
`text/plain` to your DTO, so you'll get a `415 Unsupported Media Type` — not because your code is
wrong, but because of what content-type the client happened to send. Binding that part as a plain
`String` instead, and parsing the JSON yourself inside the method, sidesteps the problem entirely
regardless of what content-type the client sends:

```java
@RequestPart(value = "metadata", required = false) String metadataJson
```

Add a new API that accepts a CSV file upload and creates a payment for every valid row in it — the
exact same way the single-transfer API creates one. That means every row goes through the same
domain validation and the same use case a single transfer goes through. No separate "bulk"
validation path, no shortcuts — a row that wouldn't pass as a single transfer must not be allowed to
slip through as part of a bulk upload either.

Don't worry yet about letting the caller choose a template. Define one fixed, default set of fields
as a JSON file under your resources, and read it into the app at startup. The shape of that default
template should live in that one resource file — not scattered across Java code as hardcoded field
definitions.

Prove it end to end: a CSV with a few valid rows and a few deliberately invalid ones should report
which succeeded and which failed, and only the valid ones should actually show up if you list
payments afterward.

### 2. Template management, backed by our own database

#### Concept: `@Embeddable` — value types with no identity of their own

You'll need this for what follows, so it's worth knowing upfront: JPA has a way to model a class
that has no identity of its own — no `@Id`, can't be looked up or saved independently, and only ever
makes sense as *part of* something else. That's an `@Embeddable`.

```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String postalCode;
    // getters/setters
}

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private Address address;
}
```

Notice `Address` has no `@Id`. There's no `AddressRepository`, and you can't fetch an `Address` on
its own. When Hibernate creates the schema for this, there's no separate `address` table at all —
`street`, `city`, and `postal_code` just become extra columns directly on the `customer` table
itself:

```
customer
--------
id
name
street
city
postal_code
```

That's the point of `@Embeddable`: it's a value — a bundle of columns that describes something about
its owner — not a "thing" with its own existence. `@Embedded` (used above, on the `Customer` side)
folds exactly **one** instance of that value into the owner's own table.

That covers a single embedded value. It doesn't yet cover what you actually need below — a *list* of
these value objects per owner, living in a table of their own rather than flattened as columns.

#### Concept: `@ElementCollection` — a *list* of embeddable values

`@Embedded` folds one value into the owner's own table. When you need **many** of that same value
type per owner — still values, still no identity of their own, just more than one — that's what
`@ElementCollection` is for. This is exactly the shape a `Template` is: a name, plus several field
definitions.

```java
@Embeddable
public class TemplateField {
    private String name;
    private int length;
    private boolean required;
    // getters/setters
}

@Entity
public class Template {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_fields", joinColumns = @JoinColumn(name = "template_id"))
    private List<TemplateField> fields = new ArrayList<>();
}
```

Two annotations are doing the work here:

- **`@ElementCollection`** — tells Hibernate "this field is a collection of values (or embeddables),
  not a collection of other entities." Unlike a real `@OneToMany` to another `@Entity`, each
  `TemplateField` has no identity of its own and can never be fetched, saved, or referenced
  independently of its owner — it only ever exists as part of this one `Template`.
- **`@CollectionTable(name = ..., joinColumns = @JoinColumn(name = ...))`** — says *where* those
  field definitions actually live. Since there's more than one per template, they can't be flattened
  into the parent's own row like `@Embedded` did — Hibernate needs a separate table
  (`template_fields` here), with a foreign key column (`template_id`) pointing back to the owning
  `Template` row. Without this annotation Hibernate will still make up a table and column name for
  you, but naming them explicitly matters here because you're also going to hand-write the matching
  Liquibase changeset yourself — the names on both sides have to agree exactly.

That resulting schema is a completely ordinary one-to-many at the SQL level:

```
templates                   template_fields
---------                   ----------------
id                          template_id (FK -> templates.id)
name                        name / length / required
```

#### Concept: eager vs. lazy loading

Notice the example above has `fetch = FetchType.EAGER` spelled out explicitly. By default, JPA
collections (`@ElementCollection` included) are **lazy**: Hibernate does *not* run the query to load
`fields` when it loads the `Template` — only the first time your code actually calls
`template.getFields()`. If that first access happens while the same database session/transaction
that loaded the `Template` is still open, it works transparently, invisibly running a second query
right when you need it. If it happens *after* that session has already closed — for example, the
`Template` was returned from a repository call, and something later (in a different method, a
different thread, or a Spring startup runner with no transaction of its own) tries to read
`fields` — Hibernate has nothing left to fetch from and throws:

```
org.hibernate.LazyInitializationException: failed to lazily initialize a collection: could not initialize proxy - no Session
```

**Eager** fetching (`fetch = FetchType.EAGER`) tells Hibernate to load the collection immediately,
right alongside the owner, so it's always fully populated the moment you get the `Template` object
back — no matter where or when you read it afterward. The tradeoff: it's always loaded, even on the
many calls where you never actually needed those field definitions, which can matter for a large or
rarely-used collection.

So which one do you want, and when:
- Reach for **lazy** (the default) when the collection can be large, or is only needed some of the
  time — loading it on every single fetch of the parent would be wasted work. You just have to make
  sure you only ever touch it from code that's guaranteed to still be inside an open session/transaction.
- Reach for **eager** when the collection is small and is *always* needed together with the owner —
  when there's no realistic scenario where the owner makes sense without it. Is a `Template` without
  its `fields` ever actually useful on its own? That's your answer for which one fits here.

Once single-transfer and bulk-transfer both work, turn "templates" into a first-class, manageable
resource in this app instead of just the one fixed default from step 1.

Add APIs to:
- create a template
- delete a template
- list all templates
- get a single template, both by id and by name

The library also ships an update-template use case — you don't need to wire it up today, just know
it's there for when this feature grows further.

Important: the file-parser library ships its own default template storage, but it's in-memory —
nothing survives a restart. You must **not** use that for this. Templates need to be persisted in
our own database, designed and migrated like any other piece of state in this app.

A template isn't one flat thing — it's a name plus an *ordered* list of field definitions, each with
its own name/length/required-ness. Design your `templates` and template-fields tables around that,
and write the Liquibase changesets for both yourself — Liquibase won't infer a table structure from
your Java annotations, so the schema and the JPA mapping have to agree on table names, the foreign
key column, and everything else, by construction, not by accident. One thing you'll need to solve
that hasn't come up yet: a CSV's columns have to line up with your template's fields in that exact
order, so figure out how to guarantee a field list mapped out of a database table comes back in the
same order it went in — that isn't guaranteed by default.

### 3. Let bulk upload actually use a chosen template

Once template management exists, go back to the bulk upload API from step 1 and let the request
optionally name which template to parse the file against.

- Caller names a template that exists → use it.
- Caller names a template that does **not** exist → reject the request; don't silently substitute
  something else. That's a mistake worth surfacing, not hiding.
- Caller doesn't name one at all → use the default template from step 1.

## Done means

- Bulk upload creates payments through the exact same domain validation and use case as the
  single-transfer API — proven by uploading a CSV with both valid and intentionally invalid rows and
  confirming only the valid ones were created.
- The default template's field definitions live in a resource file, not hardcoded in Java.
- Templates can be created, deleted, listed, and fetched by id or name via the API, and none of that
  data disappears on a restart.
- Bulk upload accepts an optional template name: an existing one is honored, an omitted one falls
  back to the default, and a named-but-nonexistent one is rejected rather than silently swapped for
  the default.
- `domain` still has zero Spring/JPA dependency, and the controller still never touches persistence
  types directly.
