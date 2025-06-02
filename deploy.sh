#!/bin/bash

# 设置变量
REMOTE_SERVER="root@8.137.85.158"
REMOTE_DIR="/root/pension"
LOCAL_DIR="."

# 构建后端项目
echo "开始构建后端项目..."
cd backend
mvn clean package -DskipTests
cd ..

# 构建前端项目
echo "开始构建前端项目..."
cd frontend
npm config set registry https://registry.npmmirror.com
npm install
npm run build
cd ..

echo "开始打包项目文件..."
# 打包项目文件
tar -czf pension-project.tar.gz \
    backend/target/*.jar \
    backend/Dockerfile \
    frontend/dist \
    frontend/Dockerfile \
    frontend/nginx.conf \
    docker-compose.yml

# 上传到服务器
echo "上传文件到服务器..."
scp pension-project.tar.gz $REMOTE_SERVER:$REMOTE_DIR/

# 在远程服务器上执行部署
echo "在远程服务器上执行部署..."
ssh $REMOTE_SERVER << 'EOF'
    cd /root/pension
    tar -xzf pension-project.tar.gz
    docker-compose down
    docker-compose up --build -d
    rm -f pension-project.tar.gz
EOF

echo "部署完成！"
echo "前端访问地址: http://8.137.85.158:3002"
echo "后端访问地址: http://8.137.85.158:8081" 