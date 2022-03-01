FROM tomcat:8.5-jdk8-openjdk-slim-buster
RUN apt-get update \
 && apt-get -y install --no-install-recommends wget \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*
COPY writer/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
