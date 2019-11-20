pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    environment {
        BDP_DATABASE_SCHEMA = "intimev2"
        BDP_DATABASE_HOST = "prod-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com"
        BDP_DATABASE_PORT = "5432"
        BDP_DATABASE_NAME = "bdp"
        BDP_DATABASE_READ_USER = "bdp_readonly"
        BDP_DATABASE_READ_PASSWORD = credentials('bdp-core-prod-database-read-password')
        BDP_DATABASE_WRITE_USER = "bdp"
        BDP_DATABASE_WRITE_PASSWORD = credentials('bdp-core-prod-database-write-password')
        BDP_READER_JWT_SECRET = credentials('bdp-core-prod-reader-jwt-secret')
    }

    stages {
        stage('Configure') {
            steps {
                // Configure DAL - API v1
                sh '''
                    cp dal/src/main/resources/META-INF/persistence.xml.dist dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.default_schema']/@value" -v ${BDP_DATABASE_SCHEMA} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.serverName']/@value" -v ${BDP_DATABASE_HOST} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.portNumber']/@value" -v ${BDP_DATABASE_PORT} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.databaseName']/@value" -v ${BDP_DATABASE_NAME} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence']/_:properties/_:property[@name='hibernate.hikari.dataSource.user']/@value" -v ${BDP_DATABASE_READ_USER} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence']/_:properties/_:property[@name='hibernate.hikari.dataSource.password']/@value" -v ${BDP_DATABASE_READ_PASSWORD} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.user']/@value" -v ${BDP_DATABASE_WRITE_USER} dal/src/main/resources/META-INF/persistence.xml
                    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.password']/@value" -v ${BDP_DATABASE_WRITE_PASSWORD} dal/src/main/resources/META-INF/persistence.xml
                    sed -i -e "s%\\(jwt.secret\\s*=\\).*\\$%\\1${BDP_READER_JWT_SECRET}%" reader/src/main/resources/META-INF/spring/application.properties
                    sed -i -e "s%\\(log4j.rootLogger\\s*=\\).*\\$%\\1INFO,R%" reader/src/main/resources/log4j.properties
                    sed -i -e "s%\\(log4j.rootLogger\\s*=\\).*\\$%\\1INFO,R%" writer/src/main/resources/log4j.properties
                    sed -i -e "s%\\(log4j.rootLogger\\s*=\\).*\\$%\\1INFO,R%" dal/src/main/resources/log4j.properties
                '''
            }
        }
        stage('Install') {
            steps {
                sh 'cd dal && mvn -B -U clean test install'
            }
        }
        stage('Build - Reader') {
            steps {
                sh 'cd reader && mvn -B -U clean test package'
            }
        }
        stage('Build - Writer') {
            steps {
                sh 'cd writer && mvn -B -U clean test package'
            }
        }
        stage('Archive') {
            steps {
                sh 'cp reader/target/reader.war reader.war'
                sh 'cp writer/target/writer.war writer.war'
                archiveArtifacts artifacts: 'reader.war', onlyIfSuccessful: true
                archiveArtifacts artifacts: 'writer.war', onlyIfSuccessful: true
            }
        }
    }
}
