# Use a base image with Java 17
FROM openjdk:19-slim

# Set working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/BookIT-0.0.1-SNAPSHOT.jar /app/BookIT-0.0.1-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 7878

# Command to run the application
CMD ["java", "-jar", "/app/BookIT-0.0.1-SNAPSHOT.jar"]
