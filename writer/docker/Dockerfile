FROM maven:3-jdk-8-alpine as build
ARG JENKINS_GROUP_ID=1000
ARG JENKINS_USER_ID=1000

RUN addgroup -g $JENKINS_GROUP_ID jenkins && \
    adduser -D -u $JENKINS_USER_ID -G jenkins jenkins

COPY docker/entrypoint-java.sh /entrypoint-java.sh

WORKDIR /code
COPY dal /code/dal
COPY GPL-3.0-header.txt /code
COPY writer /code/writer
RUN cd dal && mvn -B -U -DskipTests install
RUN cd writer/ && mvn clean package 

FROM tomcat:8.5.57-jdk8-openjdk-buster
COPY --from=build /code/writer/target/writer.war /usr/local/tomcat/webapps/ROOT.war
