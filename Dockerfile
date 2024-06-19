FROM maven:3.6.3-jdk-8 AS build

WORKDIR /app/build

COPY . /app

RUN mvn clean package

FROM openjdk:8-jre

RUN apt-get update && \
    apt-get install -y xvfb libxrender1 libxtst6 libxi6 postgresql-client x11-xkb-utils xclip && \
    apt-get clean

WORKDIR /app

COPY --from=build /app/startApp/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar /app/startApp.jar

ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=MR
ENV DB_USER=postgres
ENV DB_PASSWORD=Oor7iedi

ENV DISPLAY=host.docker.internal:0

CMD Xvfb host.docker.internal:0 -screen 0 1024x768x16 -ac & \
    setxkbmap us,ru -option 'grp:alt_shift_toggle' && \
    java -jar /app/startApp.jar