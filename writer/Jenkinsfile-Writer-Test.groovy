pipeline {
    agent any

    environment {
        BDP_DATABASE_SCHEMA = "intimev2"
        BDP_DATABASE_HOST = "test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com"
        BDP_DATABASE_PORT = "5432"
        BDP_DATABASE_NAME = "bdp"
        BDP_DATABASE_WRITE_USER = "bdp"
        BDP_DATABASE_WRITE_PASSWORD = credentials('bdp-core-test-database-write-password')

        BDP_WRITER_KEYCLOAK_CONFIG = credentials('bigdataplatform-writer-keycloak.json')
    }

    stages {
        stage('Configure') {
            steps {
                sh 'cp dal/src/main/resources/META-INF/persistence.xml.dist dal/src/main/resources/META-INF/persistence.xml'
                sh '''xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.default_schema']/@value" -v ${BDP_DATABASE_SCHEMA} dal/src/main/resources/META-INF/persistence.xml'''
                sh '''xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.serverName']/@value" -v ${BDP_DATABASE_HOST} dal/src/main/resources/META-INF/persistence.xml'''
                sh '''xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.portNumber']/@value" -v ${BDP_DATABASE_PORT} dal/src/main/resources/META-INF/persistence.xml'''
                sh '''xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.databaseName']/@value" -v ${BDP_DATABASE_NAME} dal/src/main/resources/META-INF/persistence.xml'''
                sh '''xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.user']/@value" -v ${BDP_DATABASE_WRITE_USER} dal/src/main/resources/META-INF/persistence.xml'''
                sh '''xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.password']/@value" -v ${BDP_DATABASE_WRITE_PASSWORD} dal/src/main/resources/META-INF/persistence.xml'''


                sh 'sed -i -e "s%\\(log4j.rootLogger\\s*=\\).*\\$%\\1DEBUG,R%" writer/src/main/resources/log4j.properties'
                sh 'sed -i -e "s%\\(log4j.rootLogger\\s*=\\).*\\$%\\1DEBUG,R%" dal/src/main/resources/log4j.properties'

                sh 'cat ${BDP_WRITER_KEYCLOAK_CONFIG} > writer/src/main/resources/keycloak.json'
            }
        }
        stage('Build / Deploy') {
            steps {
                sh 'docker context rm remote"'
                sh 'docker context create remote --docker "host=ssh://jenkins@63.33.73.203"'
                sh 'docker context use remote'
                sh 'docker-compose up -d --context remote'
            }
        }
    }
}
