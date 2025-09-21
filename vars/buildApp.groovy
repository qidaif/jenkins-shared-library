#!/usr/bin/groovy
// 定义一个可调用的步骤（Call）
def call(Map config = [:]) {
    // 提供默认参数
    def defaults = [
        buildTool: 'maven',
        goals: 'clean package',
        profile: null
    ]
    // 用传入的 config Map 覆盖默认值
    config = defaults + config

    // 在 Pipeline 中执行步骤
    echo "开始使用 ${config.buildTool} 构建应用程序..."
    
    if (config.buildTool == 'maven') {
        String command = "mvn ${config.goals} -DskipTests"
        if (config.profile) {
            command += " -P${config.profile}"
        }
        sh command
    } else if (config.buildTool == 'npm') {
        sh 'npm install'
        sh 'npm run build'
    } else {
        error "不支持的构建工具: ${config.buildTool}"
    }
    
    echo "构建阶段完成。"
}

// 你也可以定义非 call 的方法，但它们不会被直接作为步骤调用
def getBuildTime() {
    return new Date().format('yyyyMMdd-HHmmss')
}