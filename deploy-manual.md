# 手动部署指南

## 部署到服务器 8.137.85.158 的 Docker 容器中

### 前置条件
1. 确保服务器已安装 Docker 和 Docker Compose
2. 确保本地已构建好项目文件

### 部署步骤

#### 1. 打包项目文件
```bash
tar -czf pension-project.tar.gz \
    backend/target/*.jar \
    backend/Dockerfile \
    frontend/dist \
    frontend/Dockerfile \
    frontend/nginx.conf \
    docker-compose.yml
```

#### 2. 上传文件到服务器
```bash
scp pension-project.tar.gz root@8.137.85.158:/root/pension/
```

#### 3. 登录服务器并解压
```bash
ssh root@8.137.85.158
cd /root/pension
tar -xzf pension-project.tar.gz
```

#### 4. 停止现有容器（如果存在）
```bash
docker-compose down
```

#### 5. 构建并启动新容器
```bash
docker-compose up --build -d
```

#### 6. 验证部署
```bash
# 检查容器状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 访问地址
- 前端: http://8.137.85.158:3000
- 后端: http://8.137.85.158:8080

### 故障排除

#### 如果遇到镜像拉取问题
1. 检查网络连接
2. 使用国内镜像源
3. 手动拉取镜像：`docker pull eclipse-temurin:17-jre`

#### 如果容器启动失败
1. 检查日志：`docker-compose logs`
2. 检查端口占用：`netstat -tlnp | grep :8080`
3. 检查数据库连接配置

### 数据库配置说明
- 数据库容器名：pension-mysql
- 端口：13306
- 数据库名：pension_management_system
- 用户名：root
- 密码：Htht1234

### 网络配置
项目使用 host 网络模式，确保：
1. 服务器防火墙开放 3000 和 8080 端口
2. 数据库端口 13306 可访问