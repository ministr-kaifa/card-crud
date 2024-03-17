FROM maven:3.9.6-amazoncorretto-21-al2023 AS build
COPY core /home/app/core
COPY event-manager/pom.xml /home/app/event-manager/pom.xml
COPY api /home/app/api
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn clean package 

FROM openjdk:23-slim
COPY --from=build /home/app/api/target/*.jar /usr/local/lib/app.jar
CMD java -jar /usr/local/lib/app.jar