# syntax=docker/dockerfile:1.7

FROM maven:4.0.0-rc-7-eclipse-temurin-25-noble AS build

WORKDIR /app

COPY pom.xml .
# Загрузка зависимостей
RUN --mount=type=cache,id=maven-repo,target=/root/.m2,sharing=locked \
    mvn dependency:go-offline -B -T 1C \
    -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false

COPY src ./src
RUN --mount=type=cache,id=maven-repo,target=/root/.m2,sharing=locked \
    mvn clean package -DskipTests -o

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]