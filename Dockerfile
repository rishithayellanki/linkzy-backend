# Stage 1 — Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ─────────────────────────────────────────────────

# Stage 2 — Runtime stage
# Using eclipse-temurin:17-jre instead of alpine version
# (alpine doesn't support ARM/Apple Silicon)
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/url-shortner-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]