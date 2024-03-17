FROM maven:3.9.6-amazoncorretto-21-al2023 AS build
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn clean package

FROM openjdk:23-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
EXPOSE 8080
CMD java -jar /usr/local/lib/app.jar