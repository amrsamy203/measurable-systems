# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven \
    && mvn -q -DskipTests package \
    && JAR="$(ls target/*.jar | grep -v '\.original$' | head -n1)" \
    && cp "$JAR" /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
    && addgroup -S dispatchgrid && adduser -S dispatchgrid -G dispatchgrid
COPY --from=build /workspace/app.jar /app/dispatchgrid.jar
USER dispatchgrid
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "/app/dispatchgrid.jar"]
