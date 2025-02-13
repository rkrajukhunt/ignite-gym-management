# Use the official OpenJDK image
FROM openjdk:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the application JAR (built using Maven)
COPY target/*.jar gym-management.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "gym-management.jar"]