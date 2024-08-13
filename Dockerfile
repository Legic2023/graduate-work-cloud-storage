FROM openjdk:17-jdk-alpine

EXPOSE 8084

ADD ./target/CloudStorage-0.0.1-SNAPSHOT.jar CloudStorage.jar

ENTRYPOINT ["java", "-jar", "CloudStorage.jar"]
