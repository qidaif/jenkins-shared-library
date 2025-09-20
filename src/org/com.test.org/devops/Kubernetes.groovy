// src/org/yourcompany/devops/Kubernetes.groovy
package org.yourcompany.devops

class Kubernetes implements Serializable {
    // 必须的：Jenkins 需要序列化支持，通常传入 steps 对象
    private steps
    Kubernetes(steps) { this.steps = steps }

    def deploy(String environment, String imageTag) {
        steps.echo "正在部署到 ${environment} 环境..."
        
        // 使用 resource 目录下的 YAML 模板
        String template = steps.libraryResource 'org/yourcompany/devops/deployment.yaml'
        
        // 替换模板中的占位符
        String deploymentYaml = template
            .replaceAll('\\$\\{ENVIRONMENT\\}', environment)
            .replaceAll('\\$\\{IMAGE_TAG\\}', imageTag)
        
        // 将渲染后的 YAML 写入文件并应用
        steps.writeFile file: "deployment-${environment}.yaml", text: deploymentYaml
        steps.sh "kubectl apply -f deployment-${environment}.yaml --namespace=${environment}"
        
        steps.echo "部署到 ${environment} 完成。"
    }

    def rollback(String deploymentName, String namespace) {
        steps.sh "kubectl rollout undo deployment/${deploymentName} --namespace=${namespace}"
    }
}