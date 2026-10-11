# syntax=docker/dockerfile:1
# One image per site:
#   docker build --build-arg MODULE=meridian-contracting -t mep-meridian .
#   docker build --build-arg MODULE=axis-consultants     -t mep-axis .
#   docker build --build-arg MODULE=thermiq-hvac         -t mep-thermiq .

FROM maven:3.9-eclipse-temurin-21-alpine AS build
ARG MODULE
WORKDIR /workspace
# Every module pom is needed for the reactor; only the chosen site's sources are copied.
COPY pom.xml .
COPY mep-common/pom.xml mep-common/
COPY meridian-contracting/pom.xml meridian-contracting/
COPY axis-consultants/pom.xml axis-consultants/
COPY thermiq-hvac/pom.xml thermiq-hvac/
COPY mep-common/src mep-common/src
COPY ${MODULE}/src ${MODULE}/src
RUN --mount=type=cache,target=/root/.m2 \
    test -n "$MODULE" \
    && mvn -B -q -pl "$MODULE" -am -DskipTests package \
    && cp "$(ls "$MODULE"/target/*.jar | head -n1)" /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
    && addgroup -S site && adduser -S site -G site \
    && mkdir -p /app/data && chown site:site /app/data
COPY --from=build /workspace/app.jar /app/app.jar
USER site
EXPOSE 8080
ENV PORT=8080 \
    THERMIQ_DATA_DIR=/app/data \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
HEALTHCHECK --interval=20s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -sf "http://127.0.0.1:8080${SERVER_SERVLET_CONTEXT_PATH:-}/actuator/health" | grep -q UP || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
