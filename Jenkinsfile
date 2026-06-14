pipeline {
    agent any

    environment {
        FAILED_STAGE = "Not Started"
    }

    stages {

        stage('Build') {
            steps {
                script {
                    env.FAILED_STAGE = "Build"

                    sh '''
                    mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('Run') {
            steps {
                script {
                    env.FAILED_STAGE = "Run"

                    sh '''
                    pkill -f "book-my-ticket" || true

                    nohup java -jar target/*.jar > app.log 2>&1 &

                    sleep 15
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    env.FAILED_STAGE = "Health Check"

                    sh '''
                    curl -f http://localhost:8081
                    '''
                }
            }
        }
    }

    post {

        success {

            withCredentials([
                string(credentialsId: 'telegram-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT_ID')
            ]) {

                sh '''
                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                -d chat_id="${CHAT_ID}" \
                -d text="✅ SUCCESS

Project: Book-My-Ticket

Build Number: '${BUILD_NUMBER}'

Server: '${NODE_NAME}'

Status: Deployment Successful"
                '''
            }
        }

        failure {

            script {

                def errorLog = ""

                try {
                    errorLog = currentBuild.rawBuild.getLog(50).join('\n')
                } catch (Exception e) {
                    errorLog = "Unable to fetch console logs."
                }

                errorLog = errorLog.take(3000)

                withCredentials([
                    string(credentialsId: 'telegram-token', variable: 'TOKEN'),
                    string(credentialsId: 'telegram-chat-id', variable: 'CHAT_ID')
                ]) {

                    writeFile(
                        file: 'telegram_error.txt',
                        text: """❌ DEPLOYMENT FAILED

Project: Book-My-Ticket

Stage: ${env.FAILED_STAGE}

Build Number: ${env.BUILD_NUMBER}

Last Console Logs:

${errorLog}
"""
                    )

                    sh '''
                    curl -s -X POST \
                    "https://api.telegram.org/bot${TOKEN}/sendDocument" \
                    -F chat_id="${CHAT_ID}" \
                    -F document=@telegram_error.txt
                    '''
                }
            }
        }
    }
}
