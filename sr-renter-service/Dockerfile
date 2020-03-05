# BUILD DOCKER IMAGE
FROM openjdk:8-jre
ENV APP_HOME=/workspace/app
WORKDIR $APP_HOME
COPY build/distributions-docker/app .

EXPOSE 8084
CMD ["./bin/sr-renter-service"]