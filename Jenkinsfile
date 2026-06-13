pipeline {
    agent any

    environment {
        APP_JAR = "target/*.jar"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/vishalmolkere/Book-My-Ticket-main.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Run Application') {
            steps {
                sh '''
                    pkill -f "book-my-ticket" || true
                    nohup java -jar target/*.jar > app.log 2>&1 &
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    sleep 15
                    curl -f http://localhost:8081 || exit 1
                '''
            }
        }
    }

    post {

        success {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/sendMessage" \
                -d chat_id=<YOUR_CHAT_ID> \
                -d text="✅ CI/CD SUCCESS: Book My Ticket deployed successfully"
            '''
        }

        failure {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/sendMessage" \
                -d chat_id=<YOUR_CHAT_ID> \
                -d text="❌ CI/CD FAILED: Build or deployment failed"
            '''
        }
    }
}
