#!/bin/bash

echo "===== OneNET推送演示应用部署脚本 ====="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 构建应用
echo "正在构建应用..."
./mvnw clean package -f pom-onenet-demo.xml -DskipTests

# 检查构建是否成功
if [ ! -f "target/onenet-push-demo-0.0.1-SNAPSHOT.jar" ]; then
    echo "错误: 应用构建失败"
    exit 1
fi

# 使用Docker Compose部署
echo "正在部署应用..."
docker-compose -f docker-compose.onenet-demo.yml up -d --build

# 检查部署是否成功
if [ $? -eq 0 ]; then
    echo "===== 部署成功 ====="
    echo "应用已成功部署，可通过以下地址访问:"
    echo "- 测试界面: http://localhost:8081/onenet-demo.html"
    echo "- 健康检查: http://localhost:8081/api/demo/onenet/health"
    echo "- 推送接口: http://localhost:8081/api/demo/onenet/push"
    echo "查看应用日志: docker logs -f onenet-push-demo"
    echo "停止应用: docker-compose -f docker-compose.onenet-demo.yml down"
else
    echo "错误: 应用部署失败"
    exit 1
fi