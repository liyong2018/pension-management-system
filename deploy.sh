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

    BACKUP_DATE=$(date +%Y%m%d)
    FRONTEND_SERVICE_NAME="pension-frontend" # 请根据您的 docker-compose.yml 文件确认服务名
    BACKEND_SERVICE_NAME="pension-backend"   # 请根据您的 docker-compose.yml 文件确认服务名

    echo "开始备份容器..."

    # 备份前端容器
    FRONTEND_CONTAINER_ID=$(docker-compose ps -q $FRONTEND_SERVICE_NAME)
    if [ ! -z "$FRONTEND_CONTAINER_ID" ]; then
      echo "找到正在运行的前端容器: $FRONTEND_CONTAINER_ID"
      echo "停止前端容器..."
      docker stop $FRONTEND_CONTAINER_ID
      BACKUP_NAME_FRONTEND="${FRONTEND_SERVICE_NAME}_backup_${BACKUP_DATE}"
      echo "将前端容器 $FRONTEND_CONTAINER_ID 重命名为 $BACKUP_NAME_FRONTEND"
      docker rename $FRONTEND_CONTAINER_ID $BACKUP_NAME_FRONTEND
      echo "前端容器已备份为: $BACKUP_NAME_FRONTEND"
    else
      echo "未找到正在运行的前端容器 $FRONTEND_SERVICE_NAME。"
    fi

    # 备份后端容器
    BACKEND_CONTAINER_ID=$(docker-compose ps -q $BACKEND_SERVICE_NAME)
    if [ ! -z "$BACKEND_CONTAINER_ID" ]; then
      echo "找到正在运行的后端容器: $BACKEND_CONTAINER_ID"
      echo "停止后端容器..."
      docker stop $BACKEND_CONTAINER_ID
      BACKUP_NAME_BACKEND="${BACKEND_SERVICE_NAME}_backup_${BACKUP_DATE}"
      echo "将后端容器 $BACKEND_CONTAINER_ID 重命名为 $BACKUP_NAME_BACKEND"
      docker rename $BACKEND_CONTAINER_ID $BACKUP_NAME_BACKEND
      echo "后端容器已备份为: $BACKUP_NAME_BACKEND"
    else
      echo "未找到正在运行的后端容器 $BACKEND_SERVICE_NAME。"
    fi

    echo "容器备份流程结束。"

    echo "执行 docker-compose down..."
    docker-compose down # 这会停止并移除 docker-compose.yml 中定义的服务容器
                        # 请确保这不会影响您不希望改动的数据库容器

    echo "执行 docker-compose up --build -d..."
    docker-compose up --build -d

    echo "清理上传的压缩包..."
    rm -f pension-project.tar.gz
EOF

echo "部署完成！"
echo "前端访问地址: http://8.137.85.158:3000"
echo "后端访问地址: http://8.137.85.158:8080" 