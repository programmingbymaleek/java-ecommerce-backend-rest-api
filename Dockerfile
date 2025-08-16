# First stage: Build with Gradle
FROM gradle:8.10.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test --no-daemon

# Second stage: Run the app
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

