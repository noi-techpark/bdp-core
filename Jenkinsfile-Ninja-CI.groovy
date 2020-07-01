pipeline {
    agent {
        dockerfile {
            filename 'ninja/docker/Dockerfile'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    stages {
        stage('Test - Ninja (Reader v2)') {
            steps {
                sh 'mvn -B -U clean test'
            }
        }
    }
}
