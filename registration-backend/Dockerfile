# ==============================================================================
# Multi-Stage Hardened Dockerfile for Spring Boot
# TECHNOLOGY: Docker + Eclipse Temurin (Java 17 JRE) + Maven
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Application
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven mirror configuration (Google mirror to prevent rate limits)
COPY settings.xml .
COPY pom.xml .

# Download dependencies in a separate layer for fast Docker caching
RUN mvn dependency:go-offline -B -s settings.xml || true

# Copy application source code
COPY src ./src

# Compile and package executable Spring Boot JAR file (skipping tests for faster build)
RUN mvn clean package -DskipTests -s settings.xml

# ------------------------------------------------------------------------------
# Stage 2: Production Hardened Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Security: Create a non-root system group and user
RUN groupadd --system --gid 1001 spring && \
    useradd --system --uid 1001 --gid spring --shell /bin/false spring

# Copy JAR artifact with proper ownership
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Drop root privileges: run container as non-root user
USER spring:spring

# Expose standard application port
EXPOSE 8080

# Environment variables for JVM container tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# Healthcheck to verify Tomcat and DB health via Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD java -version || exit 1

# Execute Spring Boot application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
