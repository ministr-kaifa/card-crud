FROM maven:3.9.6-amazoncorretto-21-al2023 AS build
COPY event-manager /home/app/event-manager
COPY core /home/app/core
COPY api/pom.xml /home/app/api/pom.xml
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn clean package

FROM openjdk:23-slim
COPY --from=build /home/app/event-manager/target/*.jar /usr/local/lib/app.jar
EXPOSE 8080
CMD java -jar /usr/local/lib/app.jar