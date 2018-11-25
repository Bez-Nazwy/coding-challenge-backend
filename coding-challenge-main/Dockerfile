FROM docker.io/openjdk:10-jre-slim

WORKDIR /opt/coding-challenge
COPY build/libs/coding-challenge-main-1.0.0.jar ./

ENTRYPOINT ["java", "-jar", "coding-challenge-main-1.0.0.jar"]