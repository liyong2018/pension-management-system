#!/bin/bash

# 快速部署脚本
# 提供多种部署方式选择

SERVER="8.137.85.158"
USER="root"
REMOTE_DIR="/root/pension"

echo "=== 养老管理系统部署工具 ==="
echo "目标服务器: $SERVER"
echo ""

# 检查必要文件
check_files() {
    echo "检查项目文件..."
    
    if [ ! -f "backend/target/pension-management-backend-0.0.1-SNAPSHOT.jar" ]; then
        echo "❌ 后端JAR文件不存在，正在构建..."
        cd backend && mvn clean package -DskipTests && cd ..
        if [ $? -ne 0 ]; then
            echo "❌ 后端构建失败"
            exit 1
        fi
    fi
    
    if [ ! -d "frontend/dist" ]; then
        echo "❌ 前端构建文件不存在，正在构建..."
        cd frontend && npm install && npm run build && cd ..
        if [ $? -ne 0 ]; then
            echo "❌ 前端构建失败"
            exit 1
        fi
    fi
    
    echo "✅ 项目文件检查完成"
}

# 打包项目
package_project() {
    echo "打包项目文件..."
    tar -czf pension-deploy.tar.gz \
        backend/target/*.jar \
        backend/Dockerfile \
        frontend/dist \
        frontend/Dockerfile \
        frontend/nginx.conf \
        docker-compose.prod.yml
    
    if [ -f "pension-deploy.tar.gz" ]; then
        echo "✅ 项目打包完成: pension-deploy.tar.gz"
    else
        echo "❌ 项目打包失败"
        exit 1
    fi
}

# 部署选项1: 使用密钥认证
deploy_with_key() {
    echo "使用SSH密钥认证部署..."
    
    # 测试SSH连接
    ssh -o ConnectTimeout=5 -o BatchMode=yes $USER@$SERVER "echo 'SSH连接成功'" 2>/dev/null
    if [ $? -ne 0 ]; then
        echo "❌ SSH密钥认证失败，请配置SSH密钥或选择其他部署方式"
        return 1
    fi
    
    # 上传文件
    echo "上传文件到服务器..."
    scp pension-deploy.tar.gz $USER@$SERVER:$REMOTE_DIR/
    
    # 远程部署
    ssh $USER@$SERVER << 'EOF'
        cd /root/pension
        echo "解压文件..."
        tar -xzf pension-deploy.tar.gz
        
        echo "停止现有服务..."
        docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
        
        echo "启动新服务..."
        docker-compose -f docker-compose.prod.yml up --build -d
        
        echo "等待服务启动..."
        sleep 15
        
        echo "检查服务状态..."
        docker-compose -f docker-compose.prod.yml ps
        
        rm -f pension-deploy.tar.gz
EOF
    
    if [ $? -eq 0 ]; then
        echo "✅ 部署成功！"
        show_access_info
    else
        echo "❌ 部署失败"
    fi
}

# 显示访问信息
show_access_info() {
    echo ""
    echo "=== 部署完成 ==="
    echo "前端访问地址: http://$SERVER:3000"
    echo "后端访问地址: http://$SERVER:8080"
    echo "后端健康检查: http://$SERVER:8080/actuator/health"
    echo ""
    echo "如需查看日志，请登录服务器执行:"
    echo "ssh $USER@$SERVER"
    echo "cd $REMOTE_DIR"
    echo "docker-compose -f docker-compose.prod.yml logs -f"
}

# 显示手动部署说明
show_manual_steps() {
    echo ""
    echo "=== 手动部署步骤 ==="
    echo "1. 上传文件到服务器:"
    echo "   scp pension-deploy.tar.gz $USER@$SERVER:$REMOTE_DIR/"
    echo ""
    echo "2. 登录服务器:"
    echo "   ssh $USER@$SERVER"
    echo ""
    echo "3. 在服务器上执行:"
    echo "   cd $REMOTE_DIR"
    echo "   tar -xzf pension-deploy.tar.gz"
    echo "   docker-compose -f docker-compose.prod.yml down"
    echo "   docker-compose -f docker-compose.prod.yml up --build -d"
    echo ""
    echo "4. 检查服务状态:"
    echo "   docker-compose -f docker-compose.prod.yml ps"
    echo "   docker-compose -f docker-compose.prod.yml logs -f"
    echo ""
    show_access_info
}

# 主流程
main() {
    check_files
    package_project
    
    echo ""
    echo "请选择部署方式:"
    echo "1) 自动部署 (需要SSH密钥认证)"
    echo "2) 显示手动部署步骤"
    echo "3) 退出"
    echo ""
    read -p "请输入选择 (1-3): " choice
    
    case $choice in
        1)
            deploy_with_key
            ;;
        2)
            show_manual_steps
            ;;
        3)
            echo "退出部署"
            ;;
        *)
            echo "无效选择"
            ;;
    esac
    
    # 清理临时文件
    rm -f pension-deploy.tar.gz
}

# 执行主流程
main