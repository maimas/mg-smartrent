# BUILD DOCKER IMAGE
FROM openjdk:8-jre
ENV APP_HOME=/workspace/app
WORKDIR $APP_HOME
COPY build/distributions-docker/app .

EXPOSE 9411
CMD ["./bin/sr-logs-aggregator-service","--logging.level.zipkin2=DEBUG"]
