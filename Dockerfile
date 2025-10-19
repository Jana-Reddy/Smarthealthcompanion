# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and pom.xml to cache dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this step speeds up builds)
RUN ./mvnw dependency:go-offline -B || mvn dependency:go-offline -B

# Copy the rest of the project
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Expose port 8080 for the app
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/SmartHealthApplication.jar"]
