# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Add build timestamp to force rebuild
ARG BUILD_TIMESTAMP
ENV BUILD_TIMESTAMP=${BUILD_TIMESTAMP}

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy source code
COPY src src

# Install Maven and build the application
RUN apt-get update && \
    apt-get install -y maven curl && \
    mvn clean compile package -DskipTests -U -X && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/* /root/.m2

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose port 8080
EXPOSE 8080

# Set environment variables for runtime
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/swagger || exit 1

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/health-planner-simple-0.0.1-SNAPSHOT.jar"]
