pipeline {
    agent any

    tools {
        jdk 'jdk17'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn -v'
                sh 'mvn clean package'
            }
        }
    }
}
