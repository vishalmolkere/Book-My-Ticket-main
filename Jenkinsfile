pipeline {
    agent any

    environment {
        TOKEN = credentials('telegram-token')
        CHAT_ID = credentials('telegram-chat-id')
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    try {
                        git 'https://github.com/vishalmolkere/Book-My-Ticket-main.git'
                    } catch (Exception e) {
                        env.FAILED_STAGE = "Checkout"
                        error e.getMessage()
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        sh 'mvn clean package -DskipTests'
                    } catch (Exception e) {
                        env.FAILED_STAGE = "Build"
                        error e.getMessage()
                    }
                }
            }
        }

        stage('Run') {
            steps {
                script {
                    try {
                        sh '''
                            pkill -f book-my-ticket || true
                            nohup java -jar target/*.jar > app.log 2>&1 &
                            sleep 10
                        '''
                    } catch (Exception e) {
                        env.FAILED_STAGE = "Run"
                        error e.getMessage()
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    try {
                        sh 'curl -f http://localhost:8081'
                    } catch (Exception e) {
                        env.FAILED_STAGE = "Health Check"
                        error e.getMessage()
                    }
                }
            }
        }
    }

    post {

        success {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                -d chat_id=${CHAT_ID} \
                -d text="SUCCESS: Book My Ticket deployed successfully"
            '''
        }

        failure {
            sh '''
                ERROR_LOG=$(tail -n 20 app.log 2>/dev/null || echo "No log available")

                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                -d chat_id=${CHAT_ID} \
                -d text="FAILED at stage: ${FAILED_STAGE}. Error log: ${ERROR_LOG}"
            '''
        }
    }
}
