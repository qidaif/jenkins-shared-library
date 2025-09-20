// vars/customPipeline.groovy
def call(body) {
    // 解析传入的配置
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent any
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