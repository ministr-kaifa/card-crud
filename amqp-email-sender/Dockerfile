FROM maven:3.9.6 AS build
COPY src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:20
COPY --from=build /app/target/*.jar /app/amqp-email-sender-app.jar
CMD java -jar /app/amqp-email-sender-app.jar
