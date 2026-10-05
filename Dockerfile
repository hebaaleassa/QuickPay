# PRODUCTION image: Angular + backend in ONE jar, served on :8080.
# Build context is the repo root; "m2local" is an extra context
# (see additional_contexts in docker-compose.prod.yaml).

# Stage 1: build the Angular app
FROM node:24-alpine AS frontend
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npx ng build --configuration production

# Stage 2: build the backend jar with the Angular files inside it
FROM maven:3.9-eclipse-temurin-25 AS backend
# The backend depends on file-parser-core, which is not in any remote repository,
# so copy the already-built copy from the host's ~/.m2 into this stage's local Maven repo.
COPY --from=m2local . /root/.m2/repository/com/progressoft/training/

WORKDIR /build
COPY payments/ ./
# Spring Boot serves everything in static/ from the jar root, so the Angular
# build only has to be copied here before packaging.
COPY --from=frontend /frontend/dist/frontend/browser/ application/src/main/resources/static/
RUN mvn -B package -DskipTests

# Stage 3: small runtime image
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=backend /build/application/target/application-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
