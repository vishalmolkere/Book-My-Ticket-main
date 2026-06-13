pipeline {
    agent any

    tools {
        jdk 'jdk21'
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
