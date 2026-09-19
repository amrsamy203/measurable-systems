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
    && addgroup -S relateai && adduser -S relateai -G relateai \
    && mkdir -p /app/data/uploads \
    && chown -R relateai:relateai /app
COPY --from=build /workspace/app.jar /app/relateai.jar
USER relateai
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV RELATEAI_UPLOAD_DIR=/app/data/uploads
ENTRYPOINT ["java", "-jar", "/app/relateai.jar"]
