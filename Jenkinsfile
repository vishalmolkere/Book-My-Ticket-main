pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                script {
                    env.FAILED_STAGE = "None"
                    git 'https://github.com/vishalmolkere/Book-My-Ticket-main.git'
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
                        error "Build failed"
                    }
                }
            }
        }

        stage('Run') {
            steps {
                script {
                    sh '''
                        pkill -f book-my-ticket || true
                        nohup java -jar target/*.jar > app.log 2>&1 &
                        sleep 10
                    '''
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
                        error "Health check failed"
                    }
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
                -d text="✅ SUCCESS: Book My Ticket deployed successfully on ${NODE_NAME}"
                '''
            }
        }

        failure {
            script {

                def log = "No log available"

                try {
                    log = sh(script: "tail -n 20 app.log || true", returnStdout: true).trim()
                } catch (Exception e) {
                    log = "Failed to fetch logs"
                }

                withCredentials([
                    string(credentialsId: 'telegram-token', variable: 'TOKEN'),
                    string(credentialsId: 'telegram-chat-id', variable: 'CHAT_ID')
                ]) {
                    sh """
                    curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                    -d chat_id="${CHAT_ID}" \
                    -d text="❌ FAILED at stage: ${FAILED_STAGE}\n\nLast Logs:\n${log}"
                    """
                }
            }
        }
    }
}
