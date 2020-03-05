# BUILD DOCKER IMAGE
FROM openjdk:8-jre
ENV APP_HOME=/workspace/app
WORKDIR $APP_HOME
COPY build/distributions-docker/app .

EXPOSE 8082
CMD ["./bin/sr-user-service"]