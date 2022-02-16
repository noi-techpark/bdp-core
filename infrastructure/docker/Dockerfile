FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

FROM base as cd

# Needed for release-sdk.groovy and utils/quickrelease.sh
RUN apk add --no-cache xmlstarlet git openssh-client gawk

COPY infrastructure/docker/java-entrypoint.sh /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]

# Build
FROM base as build

WORKDIR /code
COPY . /code
RUN mvn -B -DfinalName=ROOT --projects writer --also-make package

# Archive
FROM tomcat:8.5-jdk8-openjdk-slim-buster as archive
COPY --from=build /code/writer/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Run
FROM archive as run
CMD ["catalina.sh", "run"]
