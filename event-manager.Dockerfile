FROM maven:3.9.6 AS build
COPY event-manager /app/event-manager
COPY core /app/core
COPY crud-api/pom.xml /app/crud-api/pom.xml
COPY pom.xml /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:20
COPY --from=build /app/event-manager/target/*.jar /app/event-manager-app.jar
EXPOSE 8081
CMD java -jar /app/event-manager-app.jar
