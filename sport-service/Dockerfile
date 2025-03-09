FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/sport-service-0.0.1-SNAPSHOT.jar /app/sport-service.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "/app/sport-service.jar"]
