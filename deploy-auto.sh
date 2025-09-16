#!/bin/bash

# 自动化部署脚本（需要配置SSH密钥认证）
# 使用前请确保已配置SSH密钥到服务器

# 设置变量
REMOTE_SERVER="root@8.137.85.158"
REMOTE_DIR="/root/pension"
LOCAL_DIR="."

echo "=== 开始自动化部署 ==="

# 检查SSH连接
echo "检查SSH连接..."
ssh -o ConnectTimeout=10 -o BatchMode=yes $REMOTE_SERVER "echo 'SSH连接成功'" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "错误: 无法连接到服务器，请检查："
    echo "1. 服务器地址是否正确"
    echo "2. SSH密钥是否已配置"
    echo "3. 网络连接是否正常"
    exit 1
fi

# 构建后端项目
echo "构建后端项目..."
cd backend
if ! mvn clean package -DskipTests; then
    echo "错误: 后端构建失败"
    exit 1
fi
cd ..

# 构建前端项目
echo "构建前端项目..."
cd frontend
npm config set registry https://registry.npmmirror.com
if ! npm install; then
    echo "错误: 前端依赖安装失败"
    exit 1
fi
if ! npm run build; then
    echo "错误: 前端构建失败"
    exit 1
fi
cd ..

echo "打包项目文件..."
# 打包项目文件
tar -czf pension-project.tar.gz \
    backend/target/*.jar \
    backend/Dockerfile \
    frontend/dist \
    frontend/Dockerfile \
    frontend/nginx.conf \
    docker-compose.yml

if [ ! -f "pension-project.tar.gz" ]; then
    echo "错误: 项目打包失败"
    exit 1
fi

# 上传到服务器
echo "上传文件到服务器..."
if ! scp -o ConnectTimeout=30 pension-project.tar.gz $REMOTE_SERVER:$REMOTE_DIR/; then
    echo "错误: 文件上传失败"
    exit 1
fi

# 在远程服务器上执行部署
echo "在远程服务器上执行部署..."
ssh $REMOTE_SERVER << 'EOF'
    set -e  # 遇到错误立即退出
    
    cd /root/pension
    echo "解压项目文件..."
    tar -xzf pension-project.tar.gz
    
    echo "停止现有容器..."
    docker-compose down 2>/dev/null || true
    
    echo "清理未使用的镜像..."
    docker image prune -f
    
    echo "构建并启动容器..."
    if ! docker-compose up --build -d; then
        echo "错误: 容器启动失败"
        echo "查看错误日志:"
        docker-compose logs
        exit 1
    fi
    
    echo "等待容器启动..."
    sleep 10
    
    echo "检查容器状态..."
    docker-compose ps
    
    echo "检查服务健康状态..."
    # 检查后端服务
    if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
        echo "✓ 后端服务运行正常"
    else
        echo "⚠ 后端服务可能未完全启动，请检查日志"
    fi
    
    # 检查前端服务
    if curl -f http://localhost:3000 2>/dev/null; then
        echo "✓ 前端服务运行正常"
    else
        echo "⚠ 前端服务可能未完全启动，请检查日志"
    fi
    
    echo "清理上传的压缩包..."
    rm -f pension-project.tar.gz
EOF

if [ $? -eq 0 ]; then
    echo "=== 部署成功！==="
    echo "前端访问地址: http://8.137.85.158:3000"
    echo "后端访问地址: http://8.137.85.158:8080"
    echo "后端健康检查: http://8.137.85.158:8080/actuator/health"
else
    echo "=== 部署失败！==="
    echo "请检查服务器日志获取详细错误信息"
    exit 1
fi

# 清理本地临时文件
rm -f pension-project.tar.gz

echo "=== 部署完成 ==="