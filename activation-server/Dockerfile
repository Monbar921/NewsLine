FROM maven:3.8.5-openjdk-17 AS builder
ADD . application
WORKDIR /application
RUN mvn clean package

FROM openjdk:17-jdk-slim
COPY --from=0 /application/target/activation-server.jar /application/
WORKDIR /application
ENTRYPOINT ["java", "-jar", "activation-server.jar"]