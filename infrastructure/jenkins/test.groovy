/**
 * writer - Data Writer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
pipeline {
    agent any

    environment {
        SERVER_PORT="1010"
        PROJECT = "odh-writer"
        ARTIFACT_NAME = "writer"
		LIMIT = "test"
        DOCKER_IMAGE = "755952719952.dkr.ecr.eu-west-1.amazonaws.com/$PROJECT"
        DOCKER_TAG = "$LIMIT-$BUILD_NUMBER"

        // Database Configuration
        POSTGRES_SERVER = "test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com"
        POSTGRES_DB = "bdp"
        POSTGRES_PORT = "5432"
        POSTGRES_SCHEMA = "intimev2"
        POSTGRES_USERNAME = "bdp"
        POSTGRES_PASSWORD = credentials('bdp-core-test-database-write-password')
        HIBERNATE_MAX_POOL_SIZE = "5"

        // Security
        SECURITY_ALLOWED_ORIGINS = "*"
        KEYCLOAK_URL = "https://auth.opendatahub.testingmachine.eu/auth"
        KEYCLOAK_SSL_REQUIRED = "none"
        KEYCLOAK_REALM = "noi"
        KEYCLOAK_CLIENT_ID = "odh-mobility-writer"

        // Logging
        LOG_LEVEL = "info"
        HIBERNATE_SQL_LOG = "false"
    }
    parameters{
        string(name:'bdp_version',defaultValue:'x.y.z',description:'version of dependencies to use in test deployment(must be released)');
        choice(name:'bdp_type',choices:['snapshot','release'],description:'use production ready releases or snapshots')
    }
    stages {
        stage('Configure') {
            steps {
                sh """
                    ./infrastructure/utils/quickrelease.sh '${params.bdp_type}' '${params.bdp_version}'
                    echo 'SERVER_PORT=${SERVER_PORT}' > .env
                    echo 'PROJECT=${PROJECT}' >> .env
                    echo 'ARTIFACT_NAME=${ARTIFACT_NAME}' >> .env
                    echo 'LIMIT=${LIMIT}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env

                    echo 'POSTGRES_SERVER=${POSTGRES_SERVER}' >> .env
                    echo 'POSTGRES_DB=${POSTGRES_DB}' >> .env
                    echo 'POSTGRES_PORT=${POSTGRES_PORT}' >> .env
                    echo 'POSTGRES_SCHEMA=${POSTGRES_SCHEMA}' >> .env
                    echo 'POSTGRES_USERNAME=${POSTGRES_USERNAME}' >> .env
                    echo 'POSTGRES_PASSWORD=${POSTGRES_PASSWORD}' >> .env
                    echo 'HIBERNATE_MAX_POOL_SIZE=${HIBERNATE_MAX_POOL_SIZE}' >> .env

                    echo 'SECURITY_ALLOWED_ORIGINS=${SECURITY_ALLOWED_ORIGINS}' >> .env
                    echo 'KEYCLOAK_URL=${KEYCLOAK_URL}' >> .env
                    echo 'KEYCLOAK_SSL_REQUIRED=${KEYCLOAK_SSL_REQUIRED}' >> .env
                    echo 'KEYCLOAK_REALM=${KEYCLOAK_REALM}' >> .env
                    echo 'KEYCLOAK_CLIENT_ID=${KEYCLOAK_CLIENT_ID}' >> .env

                    echo 'LOG_LEVEL=${LOG_LEVEL}' >> .env
                    echo 'HIBERNATE_SQL_LOG=${HIBERNATE_SQL_LOG}' >> .env
                """
            }
        }
        stage('Test & Build') {
            steps {
                sh """
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml push
                """
            }
        }
        stage('Deploy') {
            steps {
               sshagent(['jenkins-ssh-key']) {
                    sh """
						cd infrastructure/ansible
                        ansible-galaxy install -f -r requirements.yml
                        ansible-playbook --limit=${LIMIT} deploy.yml --extra-vars "release_name=${BUILD_NUMBER}"
                    """
                }
            }
        }
    }
}
