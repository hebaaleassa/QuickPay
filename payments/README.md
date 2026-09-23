# Day 9 — Docker & Docker Compose

## Goal

Get the app running the exact same way on any machine — yours, a teammate's, a server — without anyone
having to manually install a JDK, Postgres, or match your exact OS. That means understanding what Docker
actually is, what a Dockerfile does versus what Docker Compose does, how to package this specific app into
an image, how to run just the database in Docker while the app itself runs locally from your IDE, and how
to hand a fully working, already-built app to someone else with nothing more than one file.

## Why it matters

"Works on my machine" is the single most common source of wasted time in a team: a different Java version,
a database nobody remembers how to install, a config value only you have set correctly. Docker doesn't
fix your code — it freezes the *environment* your code runs in, so the environment stops being a variable.
Once you can dockerize a service, you can also read and reason about how every real deployment (including
the one your own team ships in production) actually works, since it's the same underlying idea — just with
more tooling layered on top for scale.

## Concept walkthrough

### 1. What Docker actually is

Docker packages an application together with everything it needs to run — the JRE, libraries, OS-level
dependencies — into a single unit called an **image**. Running that image gives you a **container**: an
isolated process that behaves identically regardless of which machine it's running on.

A few pieces of vocabulary you need before anything else makes sense:

- **Image** — a built, frozen template (like a class). Doesn't run by itself.
- **Container** — a running instance of an image (like an object instantiated from that class). You can
  run many containers from the same image.
- **The daemon (`dockerd`)** — a background service that does all the real work: pulling images, building
  layers, starting/stopping containers. The `docker` command you type is just a thin client that sends
  requests to this daemon over a socket. If the daemon isn't running, every `docker` command fails
  immediately with a connection error — `docker info` is how you check it's alive.

**Why containers instead of a VM**: a virtual machine boots an entire separate operating system — heavy,
minutes to start, gigabytes of overhead. A container shares the host machine's kernel and only isolates the
process itself (its filesystem, its network, its environment) — that's why containers start in about a
second and an image is tens/hundreds of MB instead of many GB.

### 2. Dockerfile vs Docker Compose — two different jobs

A **Dockerfile** builds *one* image. It's a recipe: start from this base, copy this file in, run this
command. It has no idea whether a database exists anywhere.

**docker-compose.yml** doesn't build anything by itself — it *orchestrates* multiple containers together as
one system: which images to use (build some, pull others), how they're networked together, what order they
start in, what ports/volumes/env vars each one gets. Plain Docker can build and run one container fine with
no compose file at all (`docker build` + `docker run`). Compose exists specifically for the moment you need
"my app" *and* "a database," wired together, started with one command, instead of several manual `docker
run`s typed in the right order by hand.

### 3. Dockerizing this app

First, the **Dockerfile** (`payments/application/Dockerfile`):

```dockerfile
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/application-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Line by line:

- **`FROM eclipse-temurin:25-jre`** — every image is built starting *from* another image. This one already
  has a Java 25 JRE installed (runtime only, no compiler — we're not compiling anything inside the image,
  just running an already-built jar).
- **`WORKDIR /app`** — sets the "current directory" inside the image for everything that follows. Same as
  `mkdir -p /app && cd /app`. It matters twice: it's where the `COPY` below actually lands the file, *and*
  it's the directory the container is sitting in when `ENTRYPOINT` runs — which is why `java -jar app.jar`
  can find `app.jar` with no path in front of it.
- **`COPY target/application-1.0.0-SNAPSHOT.jar app.jar`** — copies a file **from your machine** into the
  image. This is the one line that pulls in actual content; everything else is instructions/metadata. The
  source path is resolved relative to the *build context* (see below), not relative to the Dockerfile.
- **`EXPOSE 8080`** — documentation, not enforcement. It does **not** publish the port to your host machine
  on its own; that's what `ports:` in compose (or `-p` on `docker run`) does.
- **`ENTRYPOINT ["java", "-jar", "app.jar"]`** — the command that runs the instant the container starts;
  this process *is* the container (when it exits, the container stops). Written as a JSON array — `["java",
  "-jar", "app.jar"]` — because that's *exec form*: Docker runs `java` directly as the container's main
  process. If you wrote it as plain text (`ENTRYPOINT java -jar app.jar`, *shell form*), Docker would
  actually run `/bin/sh -c "java -jar app.jar"` instead — an extra shell process wraps your real one, and
  `docker stop`'s shutdown signal would hit that shell instead of reaching Java directly.

**Critical gotcha**: this Dockerfile does **not** build your Java code — it only copies a jar that must
already exist. The correct order, every time you change source code, is:

```bash
mvn package -DskipTests      # recompiles your code into a fresh jar in target/
docker compose build         # rebuilds the image FROM that fresh jar
```

Skip the first step, and Docker will happily package your *old* code again — you'll fix a bug, rebuild the
image, and watch the exact same bug happen again, because the jar never actually changed.

Now the **docker-compose.yml** (repo root) that runs the app together with its database:

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: paymentsdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - quickpay-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d paymentsdb"]
      interval: 5s
      timeout: 5s
      retries: 10

  app:
    build:
      context: payments/application
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: postgres,liquibase
      DB_HOST: db
      DB_PORT: "5432"
      DB_NAME: paymentsdb
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
    depends_on:
      db:
        condition: service_healthy

volumes:
  quickpay-db-data:
```

Worth being precise about a few of these:

- **`db` has no `build:`** — there's no Dockerfile for Postgres, we just pull the already-published
  `postgres:16-alpine` image. `app` has no equivalent published image, so it needs `build:`.
- **`environment:` under `db`** isn't arbitrary — those three variables are what *Postgres's own* startup
  script reads to auto-create a database/user/password the first time it boots. Every published image
  documents which env vars it understands.
- **`volumes: - quickpay-db-data:/var/lib/postgresql/data`** — a container's own filesystem is thrown away
  the moment the container is removed. That's fine for the stateless app, but fatal for a database. A
  **volume** is storage Docker manages *outside* the container's disposable filesystem, mounted into it at
  a fixed path. Postgres always writes its data to `/var/lib/postgresql/data` internally; this line
  redirects that into the named volume instead, so the data survives `docker compose down` (though **not**
  `down -v`, which deletes the volume too — that's the one command that actually wipes your data).
- **`healthcheck` + `depends_on: condition: service_healthy`** — Postgres reports "running" long before it's
  actually ready to accept connections. The healthcheck runs Postgres's own `pg_isready` tool repeatedly;
  `depends_on` uses that result to hold `app` back until the database is genuinely ready, not just started.
- **`build: context: payments/application`** — tells compose where to find the Dockerfile (it looks for a
  file literally named `application/Dockerfile` inside that folder) and is also the root that every `COPY` path inside
  that Dockerfile is resolved against — which is why `COPY target/application-1.0.0-SNAPSHOT.jar app.jar`
  finds it at `payments/application/target/...`.
- **`SPRING_PROFILES_ACTIVE`, `DB_HOST`, etc. under `app`** — same mechanism as Postgres's env vars, just
  read by *our* Spring Boot app instead. `DB_HOST: db` is the interesting one: inside a compose network,
  containers can reach each other by service name as a hostname — `db` really does resolve to the Postgres
  container from `app`'s point of view.

Bring the whole thing up with:

```bash
mvn package -DskipTests
docker compose build
docker compose up -d
```

### 4. Running just Postgres in Docker, app running locally from your IDE

Sometimes you don't want the app itself in a container at all — you want to run/debug it directly from
IntelliJ, but still don't want to install Postgres on your machine. Solution: a **second**, smaller compose
file with only the database service, no `app` at all — `local/docker-compose.yml`:

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: paymentsdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - quickpay-local-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d paymentsdb"]
      interval: 5s
      timeout: 5s
      retries: 10

volumes:
  quickpay-local-db-data:
```

Start it with `cd local && docker compose up -d`. Then in IntelliJ, run the app with the active profiles
set to `postgres,liquibase` — no environment variables needed at all, because `application-postgres.properties`
already has `localhost`/`5432`/`postgres`/`postgres` as its defaults, and this compose file exposes Postgres
on exactly that address:

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:paymentsdb}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

The `${VAR:default}` syntax means "use env var `VAR` if it's set, otherwise fall back to `default`." Nothing
sets `DB_HOST` when you run from IntelliJ, so it falls back to `localhost` — which is exactly where this
compose file published Postgres's port.

**A real gotcha to know about, because it will happen to you**: if you run this compose file's `db` and it
fails to start with "port already allocated," some *other* container is already using port 5432 (maybe the
full app+db stack from step 3 is still running). Stop that one first (`docker compose down` in whichever
directory started it) before starting this one.

### 5. Sharing your dockerized app with a friend

Once you have a working image, you can hand someone a fully working, already-built copy of your app —
without them touching your source code, Maven, or JDK version at all.

**On your machine:**
```bash
mvn package -DskipTests
docker compose build
docker save -o quickpay-app.tar quickpay-app:latest
```
`docker save` bundles the entire image — JRE, jar, everything — into one `.tar` file. Send that file however
you'd send any file (USB, shared drive, network copy). No Docker Hub account, no login, no internet needed
for this step.

**On their machine:**
```bash
docker load -i quickpay-app.tar
docker images   # confirms quickpay-app:latest is now there
```
Then either run it standalone against their own database:
```bash
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=postgres,liquibase \
  -e DB_HOST=<their-db-host> \
  quickpay-app:latest
```
or, if you also send them `docker-compose.yml`, have them change the `app` service's `build:` block to
`image: quickpay-app:latest` (since the image is already loaded locally, there's nothing left to build) and
just run `docker compose up`.

**One naming trap to watch for**: Docker Compose names containers/networks/volumes after the *directory*
your compose file lives in. If two different projects both happen to sit in a folder called the same thing
(e.g. two clones both named `quickpay`), Compose treats them as the *same project* and silently reuses the
same container names between them. This can make a stale, already-running container look like your new
build "just worked" when it never actually ran your current code at all. If something behaves confusingly,
the fix is always: `docker compose down` first (removes anything stale), then `docker compose up --build`
(forces a real rebuild) — never trust a result you didn't get from a clean slate.

## Command cheat-sheet

You'll reach for these constantly; know what each one actually does, not just when to type it.

| Command | What it does |
|---|---|
| `docker build -t <name> <path>` | Builds an image from a Dockerfile at `<path>` |
| `docker images` | Lists images you have locally |
| `docker rmi <image>` | Deletes an image |
| `docker save -o file.tar <image>` / `docker load -i file.tar` | Export/import an image as a portable file |
| `docker run <image>` | Creates and starts a container. `-d` detached, `-p host:container` publish a port, `-e KEY=value` set an env var |
| `docker ps` / `docker ps -a` | Lists running containers / all containers including stopped ones — always your first check |
| `docker stop <container>` / `docker rm <container>` | Gracefully stops / deletes a container |
| `docker logs <container>` (add `-f` to follow) | Shows everything the container has printed — your main debugging tool |
| `docker exec -it <container> bash` | Opens an interactive shell inside a running container |
| `docker port <container>` | Shows which host ports are actually published for a container |
| `docker info` | Confirms the daemon is running and reachable |
| `docker compose up` (`-d`, `--build`) | Starts everything in the compose file; `--build` forces a rebuild first |
| `docker compose down` (`-v`) | Stops and removes containers + network; `-v` also deletes volumes (wipes data) |
| `docker compose build` | Builds/rebuilds images without starting anything |
| `docker compose logs <service>` / `docker compose exec <service> <cmd>` | Same as `docker logs`/`docker exec`, by service name instead of container ID |

## Core exercises

### Exercise 1 — Dockerize this app from nothing

Starting from a clean checkout with no image built yet: write (or copy) the Dockerfile, run `mvn package`,
build the image, and run it standalone with `docker run` against a database you start separately. Confirm
with `docker logs` that it actually started, and with a real API call that it works.

### Exercise 2 — Break the "stale jar" trap on purpose

Change one small thing in a validator's error message. Rebuild the Docker image *without* running `mvn
package` first. Confirm — by hitting the running endpoint — that the old message is still there. Then run
`mvn package` and rebuild for real, and confirm the new message shows up. You should be able to explain
*why* skipping the Maven step changes nothing, in terms of what `COPY` actually does.

### Exercise 3 — Full stack with Compose

Bring up `docker-compose.yml` (app + db together) from a clean slate (`docker compose down -v` first if
anything's running), create a resource through the API, then confirm the row exists directly in Postgres
with `docker compose exec db psql -U postgres -d paymentsdb -c "select ..."`.

### Exercise 4 — Postgres-only, app from your IDE

Start only `local/docker-compose.yml`, then run the app from IntelliJ with the `postgres,liquibase`
profiles active and no environment variables set. Confirm it connects using the properties file's
defaults, not anything you configured by hand.

### Exercise 5 — Prove a volume survives, and prove `-v` destroys it

Create a payment. `docker compose down` (no `-v`), then `docker compose up` again — confirm the payment is
still there. Then `docker compose down -v` and bring it back up — confirm it's gone. Explain in your own
words which of the two commands actually deleted data, and why.

### Exercise 6 — Share your image

`docker save` your built image to a `.tar`. Remove the local image entirely (`docker rmi`). `docker load`
it back from the tar file with no rebuild at all, and run it — proving the tar really is a complete,
self-contained copy of the app.

## Stretch exercises (if you finish early)

- Give two different compose projects (e.g. two clones of this repo in differently-named folders) a
  distinct project name with `docker compose -p <name> up`, and confirm their containers/volumes never
  collide even if the folder names matched.
- Read what `docker compose exec` actually does differently from `docker run` on the same image, and when
  you'd reach for one over the other.
- Look at `docker inspect <container>` and find where the environment variables you set actually ended up.

## Done checklist

- [ ] I can explain what an image is versus what a container is, in one sentence each.
- [ ] I can explain what the Docker daemon actually is, and why every `docker` command needs it running.
- [ ] I can explain, precisely, what a Dockerfile is for versus what docker-compose.yml is for.
- [ ] I dockerized this app myself and ran it standalone with `docker run`.
- [ ] I hit the "stale jar" trap on purpose and can explain exactly why `docker build` alone didn't pick up
  my code change.
- [ ] I can explain what a volume is and why a database needs one but the app itself doesn't.
- [ ] I proved a volume survives `docker compose down` but not `docker compose down -v`.
- [ ] I ran the app from my IDE against a Postgres that only exists in Docker, using nothing but the
  properties file's own defaults.
- [ ] I shared a built image with `docker save`/`docker load` and ran it with zero rebuild on the
  receiving end.
- [ ] I can name at least six `docker`/`docker compose` commands from memory and say what each does.

## Suggested resources

- Docker docs, "What is a container?" — https://www.docker.com/resources/what-container/
- Docker docs, Dockerfile reference — https://docs.docker.com/reference/dockerfile/
- Docker Compose docs, Compose file reference — https://docs.docker.com/compose/compose-file/
- Docker docs, "Volumes" — https://docs.docker.com/engine/storage/volumes/
- Docker docs, `docker save` / `docker load` — https://docs.docker.com/reference/cli/docker/image/save/
