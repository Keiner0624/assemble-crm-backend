FROM maven:3.9.12-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S assemble && adduser -S assemble -G assemble

WORKDIR /app
COPY --from=build /workspace/target/assemble-crm-1.0.0.jar app.jar

USER assemble
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
