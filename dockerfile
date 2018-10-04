FROM debian:9

ARG JENKINS_GROUP_ID
ARG JENKINS_USER_ID

RUN groupadd --gid $JENKINS_GROUP_ID jenkins && \
    useradd --uid $JENKINS_USER_ID --gid $JENKINS_GROUP_ID jenkins && \
    mkdir -p /home/jenkins && \
    chown -R jenkins:jenkins /home/jenkins && \
    chmod -R 750 /home/jenkins

RUN apt-get update && \
    apt-get install -y git openjdk-8-jdk maven

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]
