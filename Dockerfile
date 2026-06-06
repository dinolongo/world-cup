# Multi-stage build for World Cup 2026 Backend
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Gradle wrapper and build files
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR file from builder
COPY --from=builder /build/build/libs/world-cup-backend.jar app.jar

# Expose port (Render will set PORT env var)
EXPOSE 8080

# Health check (use PORT env var if available, default to 8080)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application (support Render's PORT env var)
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.port=${PORT:-8080}", \
    "-jar", \
    "/app/app.jar"]
