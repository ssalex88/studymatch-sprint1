FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/studymatch-backend-jar-with-dependencies.jar ./studymatch-backend.jar

EXPOSE 8080
CMD ["java", "-jar", "studymatch-backend.jar"]
