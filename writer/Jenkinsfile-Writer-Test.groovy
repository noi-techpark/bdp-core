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
        stage('Build') {
            steps {
                sh 'cd writer && docker-compose build'
            }
        }
        stage('Deploy') {
            steps {
                sh 'cd writer && docker-compose --context test-docker-01 up -d'
            }
        }
    }
}
