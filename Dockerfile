# Frontend
FROM node:lts AS frontend

RUN mkdir /build

COPY ./client/package.json /build/package.json
COPY ./client/package-lock.json /build/package-lock.json

WORKDIR /build
RUN npm install

COPY ./client/src /build/src
COPY ./client/public /build/public
COPY ./client/babel.config.js /build/babel.config.js
COPY ./client/.eslintrc.js /build/.eslintrc.js
COPY ./client/.browserslistrc /build/.browserslistrc

RUN npm run build


# Backend
FROM gradle:7.4.2-jdk17 AS backend

RUN mkdir /app

WORKDIR /app

COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./src /app/src

COPY --from=frontend /build/dist /app/src/main/resources/static
RUN mv /app/src/main/resources/static/index.html /app/src/main/resources/templates/index.html

RUN gradle bootJar


# Host
FROM openjdk:17-slim

RUN mkdir /app
COPY --from=backend /app/build/libs/*.jar /app/application.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/application.jar"]

EXPOSE 8080:8080

