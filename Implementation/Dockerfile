FROM gradle:ubi9 AS builder

WORKDIR /
COPY settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY app ./app

RUN gradle dependencies --no-daemon

COPY app/src ./src

RUN gradle bootJar --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder app/build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]