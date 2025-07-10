# Use a lightweight JDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the already built JAR from Maven's target directory
COPY target/*.jar app.jar

# Expose the correct port (change accordingly per service)
EXPOSE 8082

# Run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
