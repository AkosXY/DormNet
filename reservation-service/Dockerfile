FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/reservation-service-0.0.1-SNAPSHOT.jar /app/reservation-service.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "/app/reservation-service.jar"]
