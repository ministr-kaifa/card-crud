FROM maven:3.9.6 AS build
COPY core /app/core
COPY event-manager/pom.xml /app/event-manager/pom.xml
COPY crud-api /app/crud-api
COPY pom.xml /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:20
COPY --from=build /app/crud-api/target/*.jar /app/crud-api-app.jar
EXPOSE 8080
CMD java -jar /app/crud-api-app.jar
