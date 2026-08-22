# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first so dependency layer is cached separately from source changes
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render (and most platforms) inject $PORT at runtime; application.properties
# already reads server.port=${PORT:8080}, so no extra config needed here.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
