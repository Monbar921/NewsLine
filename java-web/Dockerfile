FROM maven:3.8.5-openjdk-17 AS builder
ADD . application
WORKDIR /application
RUN mvn clean package

FROM openjdk:17-jdk-slim
COPY --from=0 /application/target/newsline-test-task.war /application/
WORKDIR /application
ENTRYPOINT ["java", "-jar", "newsline-test-task.war"]