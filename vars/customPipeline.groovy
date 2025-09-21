// vars/customPipeline.groovy
def call(body) {
    // 解析传入的配置
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent any
        environment {
            // 添加 Node.js 的安装路径到 PATH
            PATH = "/usr/local/bin:/usr/bin:/bin:${env.PATH}"
        }
        
        stages {
            stage('Build') {
                steps {
                    script {
                        buildApp(config.buildConfig ?: [:])
                    }
                }
            }
            stage('Test') {
                steps {
                    sh config.testCommand ?: 'echo "No tests configured"'
                }
            }
            stage('Deploy') {
                when { branch 'main' }
                steps {
                    script {
                        if (config.deployToStaging) {
                            def k8s = new org.yourcompany.devops.Kubernetes(this)
                            k8s.deploy('staging', "${config.appName}:${env.BUILD_TAG}")
                        }
                    }
                }
            }
        }
    }
}