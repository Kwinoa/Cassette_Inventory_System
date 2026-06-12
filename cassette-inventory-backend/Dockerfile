# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Added 'app.jar' at the end as the destination name
COPY --from=build /app/target/Cassette-Inventory-System-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Simplified the entrypoint to use the renamed 'app.jar'
ENTRYPOINT ["java", "-jar", "app.jar"]