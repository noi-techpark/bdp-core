FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

# Dev
FROM base as dev

# Build
FROM base as build

WORKDIR /code
COPY dal /code/dal
COPY GPL-3.0-header.txt /code
RUN cd dal && mvn -B -U -DskipTests install && rm -rf dal
COPY writer/src /code/src
COPY writer/pom.xml /code/
RUN mvn package -DfinalName=ROOT

# Archive
FROM tomcat:8.5-jdk8-openjdk-slim-buster as archive
COPY --from=build /code/target/writer.war /usr/local/tomcat/webapps/ROOT.war

# Run
FROM archive as run
CMD ["catalina.sh", "run"]


