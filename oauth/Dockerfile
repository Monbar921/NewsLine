FROM maven:3.8.5-openjdk-17 AS builder
ADD . application
WORKDIR /application
RUN mvn clean package

FROM openjdk:17-jdk-slim as publisher
COPY --from=0 /application/target/auth-server.jar /application/
WORKDIR /application
ENTRYPOINT ["java", "-jar", "auth-server.jar"]