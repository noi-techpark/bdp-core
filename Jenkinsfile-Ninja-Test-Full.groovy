pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    environment {
        NINJA_ROOT_PATH = "ninja"
        NINJA_LOGGING_FILE = "/var/log/opendatahub/ninja.log"
        NINJA_KEYCLOAK_SERVERURL = "https://auth.opendatahub.testingmachine.eu/auth/"

        NINJA_DATABASE_SCHEMA = "intimev2"
        NINJA_DATABASE_HOST = "prod-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com"
        NINJA_DATABASE_PORT = "5432"
        NINJA_DATABASE_NAME = "bdp"
        NINJA_DATABASE_READ_USER = "bdp_readonly"
        NINJA_DATABASE_READ_PASSWORD = credentials('bdp-core-prod-database-read-password')

        NINJA_SWAGGER_SERVER_URL = "https://mobility.api.opendatahub.testingmachine.eu"
    }

    stages {
        stage('Configure') {
            steps {
                sh '''
                    cp "${NINJA_ROOT_PATH}/src/main/resources/application.properties.dist" "${NINJA_ROOT_PATH}/src/main/resources/application.properties"
                    sed -i -e "s%\\(logging.file\\s*=\\).*\\$%\\1${NINJA_LOGGING_FILE}%" ${NINJA_ROOT_PATH}/src/main/resources/application.properties
                    sed -i -e "s%\\(logging.level.root\\s*=\\).*\\$%\\1DEBUG%" ${NINJA_ROOT_PATH}/src/main/resources/application.properties
                    sed -i -e "s%\\(logging.level.org.springframework.jdbc.core\\s*=\\).*\\$%\\1TRACE%" ${NINJA_ROOT_PATH}/src/main/resources/application.properties
                    sed -i -e "s%\\(keycloak.auth-server-url\\s*=\\).*\\$%\\1 ${NINJA_KEYCLOAK_SERVERURL}%" ${NINJA_ROOT_PATH}/src/main/resources/application.properties

                    sed -i -e "s%__ODH_SERVER_URL__%${NINJA_SWAGGER_SERVER_URL}%" ${NINJA_ROOT_PATH}/src/main/resources/openapi3.yml

                    cp "${NINJA_ROOT_PATH}/src/main/resources/database.properties.dist" "${NINJA_ROOT_PATH}/src/main/resources/database.properties"
                    sed -i -e "s%\\(username\\s*=\\).*\\$%\\1 ${NINJA_DATABASE_READ_USER}%" ${NINJA_ROOT_PATH}/src/main/resources/database.properties
                    sed -i -e "s%\\(password\\s*=\\).*\\$%\\1 ${NINJA_DATABASE_READ_PASSWORD}%" ${NINJA_ROOT_PATH}/src/main/resources/database.properties
                    sed -i -e "s%\\(jdbcUrl\\s*=\\).*\\$%\\1 jdbc:postgresql://${NINJA_DATABASE_HOST}:${NINJA_DATABASE_PORT}/${NINJA_DATABASE_NAME}?currentSchema=${NINJA_DATABASE_SCHEMA},public%" ${NINJA_ROOT_PATH}/src/main/resources/database.properties
                '''
            }
        }
        stage('Build - Ninja') {
            steps {
                sh 'cd ${NINJA_ROOT_PATH} && mvn -B -U clean test package'
            }
        }
        stage('Archive') {
            steps {
                sh 'cp ${NINJA_ROOT_PATH}/target/v2.war v2.war'
                archiveArtifacts artifacts: 'v2.war', onlyIfSuccessful: true
            }
        }
    }
}
