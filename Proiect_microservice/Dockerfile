FROM openjdk:17-jdk-alpine

# Copy the application JAR file
COPY target/card-0.0.1-SNAPSHOT.jar app.jar

# Copy the secondary properties file
COPY src/main/resources/docker.properties docker.properties

# Run the application with the secondary config location specified
CMD ["java", "-jar", "/app.jar", "--spring.config.location=classpath:/docker.properties"]