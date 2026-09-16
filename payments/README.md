# Day 3 — Schema Migrations with Liquibase

## Goal

Understand why `ddl-auto` is a liability once real teams/real data are involved, replace it with a
real, versioned Liquibase changelog, and be able to write a changeset in **both** YAML and XML — the
two formats you'll actually see in real projects.

## Why it matters

Yesterday you saw `ddl-auto` create your `students` table for you — convenient, but with no record
of *what* changed, no safety against real data, and no rollback. A real team needs the opposite:
every schema change as an explicit, reviewable, versioned file a teammate can read in a pull request,
applied identically to every environment, in the same order, every time. That's what a migration tool
gives you. Liquibase is one implementation of that idea (Flyway is another) — the concept matters
more than the specific tool.

## Concept walkthrough

### 1. Changelogs and changesets

A **changeset** is one atomic, identified schema change. A **changelog** is an ordered list of
changesets. Liquibase tracks, per database, exactly which changesets have already run — so running
it again is always safe; it only applies what's new.

Every changeset needs a unique `id` (unique per `author`, by convention) — that pair is how Liquibase
knows "have I already run this."

### 2. The same changeset, in both formats

**YAML** — `db/changelog/changes/001-create-students-table.yaml`:

```yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-students-table
      author: your-name
      changes:
        - createTable:
            tableName: students
            columns:
              - column:
                  name: id
                  type: bigint
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: email
                  type: varchar(255)
              - column:
                  name: grade
                  type: varchar(50)
```

**XML** — `db/changelog/changes/001-create-students-table.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="001-create-students-table" author="your-name">
        <createTable tableName="students">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="email" type="VARCHAR(255)"/>
            <column name="grade" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>

</databaseChangeLog>
```

Same change, same `id`/`author`, same effect — just two different ways to write it. Most teams pick
one format and stick with it; you're seeing both so you can read either one in a real codebase.

### 3. The master changelog pattern

Rather than one enormous file, one **master changelog** includes individual changeset files, in
order. Each schema change stays its own small file, added once and never edited afterward — if
something needs to change, you add a *new* changeset, you don't rewrite one that may have already run
somewhere.

**YAML** — `db/changelog/db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/db/changes/001-create-students-table.yaml
  - include:
      file: db/db/changes/002-add-phone-column.yaml
```

**XML** — `db/changelog/db.changelog-master.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <include file="db/db/changes/001-create-students-table.xml" relativeToChangelogFile="false"/>
    <include file="db/db/changes/002-add-phone-column.xml" relativeToChangelogFile="false"/>

</databaseChangeLog>
```

### 4. Rollback

A changeset can declare how to undo itself:

**YAML:**
```yaml
databaseChangeLog:
  - changeSet:
      id: 002-add-phone-column
      author: your-name
      changes:
        - addColumn:
            tableName: students
            columns:
              - column:
                  name: phone
                  type: varchar(20)
      rollback:
        - dropColumn:
            tableName: students
            columnName: phone
```

**XML:**
```xml
<changeSet id="002-add-phone-column" author="your-name">
    <addColumn tableName="students">
        <column name="phone" type="VARCHAR(20)"/>
    </addColumn>
    <rollback>
        <dropColumn tableName="students" columnName="phone"/>
    </rollback>
</changeSet>
```

Some changes (like `addColumn`) have an automatic inferred rollback if you omit the `rollback` block
— Liquibase can figure out that undoing "add a column" is "drop that column." Others don't, and
Liquibase fails if you try to roll them back without one. Write `rollback` explicitly as a habit, not
just when it's required.

### 5. Steps to actually turn this on

1. **Add the dependency** — `org.liquibase:liquibase-core` to `pom.xml`. It isn't pulled in by
   `spring-boot-starter-data-jpa` automatically; you add it yourself.
2. **Turn off `ddl-auto`**.
3. **Create the master changelog** at `src/main/resources/db/db/db.changelog-master.yaml`
   (Spring Boot looks for this exact path by default). Using XML instead? Point Spring Boot at it
   explicitly:
   ```properties
   spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
   ```
4. **Create your first changeset file** and `include` it in the master changelog.
5. **Start the app.** Spring Boot runs Liquibase automatically, before the rest of the application
   context finishes starting. Liquibase creates its own tracking table, `DATABASECHANGELOG`, checks
   which changesets are new, and applies only those, in order.
6. **Verify** — open the H2 console: the `students` table should exist with the right columns, and
   `DATABASECHANGELOG` should list your changeset as applied.

### Common mistakes

- **Reusing an `id` for a changed changeset.** Once a changeset has run anywhere, editing it in place
  doesn't do what you'd expect — Liquibase sees the same `(id, author)` and considers it already
  applied, so your edit is silently ignored on any database that already ran the original. Add a new
  changeset instead.
- **Forgetting to add the new changeset file to the master changelog's `include` list.** It exists on
  disk, Liquibase never sees it, and it's easy to assume it ran when it didn't.
- **Type mismatches with your JPA entity** (e.g. `varchar(64)` in the changelog vs. no length limit
  assumed in code). `ddl-auto=validate` catches a lot of these at startup — read the failure message,
  it usually names the exact column and mismatch.


## Done checklist

- [x] I can explain at least two concrete problems with `ddl-auto` that Liquibase solves.
- [x] I can explain the difference between a changelog and a changeset.
- [x] I can explain the master-changelog-includes-changesets pattern, and why changesets aren't
  edited after the fact.
- [x] I can write the same changeset in both YAML and XML.
- [x] Starting against a clean database produces the correct schema, with `DATABASECHANGELOG`
  showing both changesets applied.
- [x] All existing endpoints still work end to end.
- [x] At least one changeset has an explicit (not inferred) `rollback`.

## Suggested resources

- Liquibase official documentation — https://docs.liquibase.com/
- Spring Boot reference, "Database Initialization" (Liquibase section) — https://docs.spring.io/spring-boot/reference/howto/data-initialization.html
- Baeldung, "Database Migrations with Liquibase" — https://www.baeldung.com/liquibase-refactor-schema-of-java-app
