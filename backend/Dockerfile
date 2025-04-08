# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["/wait-for-it.sh", "db:3306", "-t", "15", "--", "java", "-jar", "app.jar"]
