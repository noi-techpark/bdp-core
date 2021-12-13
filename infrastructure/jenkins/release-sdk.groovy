pipeline {
    agent {
        dockerfile {
            filename 'infrastructure/docker/Dockerfile'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins) --target cd'
        }
    }

    parameters {
        string(
            name: 'TAG',
            defaultValue: '1.0.0',
            description: 'Tag pushed to Git. We add the postfix "-SNAPSHOT" for you if you choose the development branch, so write only a plain version'
        )
        gitParameter name: 'BRANCH', branchFilter: 'origin/(.*)', defaultValue: 'development', type: 'PT_BRANCH', description: 'Choose either master or development as branch. The tag and version bump commit will be made on it. If you want to make a RELEASE use the master branch, if you want to make a SNAPSHOT release use development.'
    }

    // Do not rename keys AWS_ACCESS_KEY or AWS_SECRET_KEY, because the maven AWS plugin needs them to retrieve meta data
    environment {
        AWS_ACCESS_KEY = credentials('s3_repo_username')
        AWS_SECRET_KEY = credentials('s3_repo_password')
        REL_TYPE = "${params.BRANCH == "master" ? "release" : "snapshot"}"
        VERSION = "${params.BRANCH == "master" ? "${params.TAG}" : "${params.TAG}-SNAPSHOT"}"
        S3_REPO_ID = "${params.BRANCH == "master" ? "maven-repo.opendatahub.bz.it-release": "maven-repo.opendatahub.bz.it-snapshot"}"
    }

    stages {
        stage('Check if BRANCH is correct and TAG exists (only master)') {
            when {expression {return "${params.BRANCH}" == "master"}}
            steps {
                sh """
                    echo FAIL IF TAG ${VERSION} ALREADY EXISTS!
                    git rev-parse ${VERSION} &> /dev/null && false || true
                """
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
                sh "./infrastructure/utils/quickrelease.sh $REL_TYPE '${params.TAG}'"
            }
        }
        stage('Deploy DTO') {
            steps {
                sh '''
                    cd dto
                    mvn -B -U clean test install deploy
                '''
            }
        }
        stage('Deploy DC-INTERFACE') {
            steps {
                sh '''
                    cd dc-interface
                    mvn -B -U clean test deploy
                '''
            }
        }
        stage('Tag (only master)') {
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
