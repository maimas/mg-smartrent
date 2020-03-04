## RUN BUILD
#FROM openjdk:8 AS BUILD_STAGE
#ENV BUILD_HOME=/workspace/app-source/
#WORKDIR $BUILD_HOME
#COPY gradle $BUILD_HOME/gradle
#COPY gradlew gradlew.bat $BUILD_HOME
#COPY build.gradle gradlew gradlew.bat $BUILD_HOME
#COPY src $BUILD_HOME
#RUN ./gradlew buildDockerImage
#
#

# BUILD DOCKER IMAGE
FROM openjdk:8-jre
ENV APP_HOME=/workspace/app
WORKDIR $APP_HOME
COPY build/distributions-docker/app .

EXPOSE 8081
CMD ["./bin/sr-gateway-api-service"]