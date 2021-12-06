pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    parameters {
        string(name: 'TAG', defaultValue: '1.0.0', description: 'Tag pushed to Git. We add the postfix "-SNAPSHOT" for you if you choose the development branch, so write only a plain version')
        gitParameter name: 'BRANCH', branchFilter: 'origin/(.*)', defaultValue: 'development', type: 'PT_BRANCH', description: 'Choose either master or development as branch. The tag and version bump commit will be made on it. If you want to make a RELEASE use the master branch, if you want to make a SNAPSHOT release use development.'
    }

    // Do not rename keys AWS_ACCESS_KEY or AWS_SECRET_KEY, because the maven AWS plugin needs them to retrieve meta data
    environment {
        AWS_ACCESS_KEY=credentials('s3_repo_username')
        AWS_SECRET_KEY=credentials('s3_repo_password')
        REL_TYPE="${params.BRANCH == "development" ? "snapshot" : "release"}"
        VERSION="${params.BRANCH == "development" ? "${params.TAG}-SNAPSHOT" : "${params.TAG}"}"
        S3_REPO_ID="${params.BRANCH == "development" ? "maven-repo.opendatahub.bz.it-snapshot" : "maven-repo.opendatahub.bz.it-release"}"
    }

    stages {
        stage('Check if BRANCH is correct and TAG exists') {
            steps {
                sh "test '${params.BRANCH}' = 'development' -o '${params.BRANCH}' = 'master' || false"
                sh "test '${params.BRANCH}' = 'development' || { echo CHECK IF TAG ${VERSION} ALREADY EXISTS; git rev-parse ${VERSION} >/dev/null 2>&1 && false || true; }"
            }
        }
        stage('Configure') {
            steps {
                sh 'sed -i -e "s/<\\/settings>$//g\" ~/.m2/settings.xml'
                sh 'echo "    <servers>" >> ~/.m2/settings.xml'
                sh 'echo "        <server>" >> ~/.m2/settings.xml'
                sh 'echo "            <id>${S3_REPO_ID}</id>" >> ~/.m2/settings.xml'
                sh 'echo "            <username>${AWS_ACCESS_KEY}</username>" >> ~/.m2/settings.xml'
                sh 'echo "            <password>${AWS_SECRET_KEY}</password>" >> ~/.m2/settings.xml'
                sh 'echo "        </server>" >> ~/.m2/settings.xml'
                sh 'echo "    </servers>" >> ~/.m2/settings.xml'
                sh 'echo "</settings>" >> ~/.m2/settings.xml'
            }
        }
        stage('Preparation for release') {
            steps {
                sh "./quickrelease.sh $REL_TYPE '${params.TAG}'"
            }
        }
        stage('Deploy dto') {
            steps {
		        sh 'cd dto/ && mvn -B -U clean test deploy'
            }
        }
        stage('Deploy dc-interface') {
            steps {
		        sh 'cd dc-interface/ && mvn -B -U clean test deploy'
            }
        }
        stage('Tag') {
            when {expression {return "${params.BRANCH}" == "master"}}
            steps {
                sshagent (credentials: ['jenkins_github_ssh_key']) {
                    sh "git config --global user.email 'info@opendatahub.bz.it'"
                    sh "git config --global user.name 'Jenkins'"
                    sh "git commit -a -m 'Version ${VERSION}' --allow-empty"
                    sh "git tag -d ${VERSION} || true"
                    sh "git tag -a ${VERSION} -m ${VERSION}"
                    sh "mkdir -p ~/.ssh"
                    sh "ssh-keyscan -H github.com >> ~/.ssh/known_hosts"
                    sh "git push origin HEAD:${params.BRANCH} --follow-tags"
                }
            }
        }
    }
}
