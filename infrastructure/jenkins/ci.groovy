pipeline {
    agent {
        dockerfile {
            filename 'infrastructure/docker/Dockerfile'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins) --target base'
        }
    }

    stages {
        stage('Test - DTO') {
            steps {
                sh 'cd dto && mvn -B -U clean test'
            }
        }
        stage('Test - DC-INTERFACE') {
            steps {
                sh '''
                    (cd dto && mvn -B -U -DskipTests install)
                    (cd dc-interface && mvn -B -U clean test)
                '''
            }
        }
        stage('Test - Writer') {
            steps {
                sh '''
                    (cd dto && mvn -B -U -DskipTests install)
                    (cd writer && mvn -B -U clean test)
                '''
            }
        }
    }
}
