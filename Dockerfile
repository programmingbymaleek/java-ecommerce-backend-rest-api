# ---- Build stage ----
FROM openjdk:21-jdk-slim AS build
WORKDIR /app

COPY gradlew ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew --no-daemon -q dependencies

COPY src ./src
RUN ./gradlew --no-daemon -q clean bootJar

# ---- Run stage ----
FROM openjdk:21-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
