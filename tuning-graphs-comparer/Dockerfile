#Depending on your needs, choose a proper base image. Eg. the slim version does not work with apache POI
#FROM maven:3.8.6-eclipse-temurin-11-alpine
FROM maven:3.8.6-jdk-11

WORKDIR /application

COPY src ./src
COPY pom.xml ./pom.xml
COPY lib ./lib

RUN mvn clean package

WORKDIR /

CMD java -jar /application/target/*.jar
