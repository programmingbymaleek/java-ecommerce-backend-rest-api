# ---- Build stage (uses Gradle with JDK 21) ----
FROM gradle:8.10.1-jdk21 AS build
WORKDIR /app
COPY . .
# Build a fat jar (Spring Boot's bootJar); skip tests for speed
RUN gradle clean bootJar -x test --no-daemon

# ---- Run stage (lightweight JRE 21) ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the jar produced by bootJar (lives in build/libs/)
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


