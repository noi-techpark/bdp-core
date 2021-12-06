pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    stages {
        stage('Test - DTO') {
            steps {
                sh 'cd dto && mvn -B -U clean test install'
            }
        }
        stage('Test - DC-INTERFACE') {
            steps {
                sh 'cd dc-interface && mvn -B -U clean test install'
            }
        }
        stage('Test - Writer') {
            steps {
                sh 'cd writer && mvn -B -U clean test'
            }
        }
    }
}
