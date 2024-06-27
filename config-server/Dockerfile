FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/config-server-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/secret.properties secret.properties
ENTRYPOINT ["java", "-jar","/app.jar", "--spring.config.import=classpath:/secret.properties"]