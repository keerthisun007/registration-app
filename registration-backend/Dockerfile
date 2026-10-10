# ==============================================================================
# Multi-Stage Dockerfile for Spring Boot Backend
# TECHNOLOGY: Docker + Eclipse Temurin (Java 17) + Maven
# ==============================================================================

# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven mirror settings to bypass HTTP 429 rate limits
COPY settings.xml .
# Copy pom.xml and download dependencies
COPY pom.xml .
# Copy project source code
COPY src ./src

# Compile and package Spring Boot executable JAR file using Google Maven mirror
RUN mvn clean package -DskipTests -s settings.xml

# Stage 2: Production Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy packaged JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot Tomcat port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
