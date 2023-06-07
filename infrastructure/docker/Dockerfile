FROM eclipse-temurin:17-jre-alpine
WORKDIR /bdp
RUN apk add --no-cache wget
COPY writer/target/bdp.jar /bdp/bdp.jar
CMD [ "java", "-jar", "bdp.jar" ]
