# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and source files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built artifact from build stage
COPY --from=build /app/target/BlackJackAPI-*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Environment variables with defaults
ENV DB_URL=r2dbc:mysql://mysql:3306/blackjack
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV JWT_SECRET=dGhpcy1pcy1hLXNlY3VyZS1qd3Qtc2VjcmV0LWtleS1mb3ItYmxhY2tqYWNrLWFwaS1wcm9qZWN0LTI1Ng==
ENV JWT_EXPIRATION=86400000
ENV SERVER_PORT=8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]