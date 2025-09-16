#!/bin/bash

# 数据库同步脚本 - 将本地MySQL数据同步到远程服务器
# 使用方法: ./sync_database.sh

# 设置变量
REMOTE_SERVER="root@8.137.85.158"
LOCAL_DB_HOST="127.0.0.1"
LOCAL_DB_PORT="3306"
LOCAL_DB_NAME="pension_management_system"
LOCAL_DB_USER="root"
LOCAL_DB_PASSWORD="Htht1234"

REMOTE_DB_HOST="localhost"
REMOTE_DB_PORT="13306"
REMOTE_DB_NAME="pension_management_system"
REMOTE_DB_USER="root"
REMOTE_DB_PASSWORD="Htht1234"
REMOTE_CONTAINER_NAME="pension-mysql"

# 备份文件名
BACKUP_FILE="pension_db_backup_$(date +%Y%m%d_%H%M%S).sql"
TEMP_DIR="/tmp"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${GREEN}开始数据库同步流程...${NC}"
echo -e "${YELLOW}======================================${NC}"

# 1. 检查本地数据库连接
echo -e "${CYAN}1. 检查本地数据库连接...${NC}"

# 检查Docker是否运行
if ! docker ps --filter "name=pension-mysql" --format "table {{.Names}}\t{{.Status}}" | grep -q "pension-mysql"; then
    echo -e "${RED}❌ 错误: 本地MySQL容器未运行${NC}"
    echo -e "${YELLOW}请先启动容器: docker-compose up -d pension-mysql${NC}"
    exit 1
fi

# 测试数据库连接
if ! docker exec pension-mysql mysql -h$LOCAL_DB_HOST -P$LOCAL_DB_PORT -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD -e "SELECT 1" >/dev/null 2>&1; then
    echo -e "${RED}❌ 错误: 无法连接到本地数据库${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 本地数据库连接成功${NC}"

# 2. 导出本地数据库
echo -e "${CYAN}2. 导出本地数据库...${NC}"

docker exec pension-mysql mysqldump \
    -h$LOCAL_DB_HOST -P$LOCAL_DB_PORT -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    --hex-blob \
    --default-character-set=utf8mb4 \
    $LOCAL_DB_NAME > $BACKUP_FILE

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 错误: 数据库导出失败${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 数据库导出成功: $BACKUP_FILE${NC}"

# 3. 上传备份文件到远程服务器
echo -e "${CYAN}3. 上传备份文件到远程服务器...${NC}"

scp $BACKUP_FILE $REMOTE_SERVER:$TEMP_DIR/
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 错误: 文件上传失败${NC}"
    rm -f $BACKUP_FILE
    exit 1
fi
echo -e "${GREEN}✅ 文件上传成功${NC}"

# 4. 在远程服务器上执行数据库恢复
echo -e "${CYAN}4. 在远程服务器上执行数据库恢复...${NC}"

ssh $REMOTE_SERVER << EOF
echo "检查远程MySQL容器状态..."
if ! docker ps | grep -q $REMOTE_CONTAINER_NAME; then
    echo "❌ 错误: 远程MySQL容器未运行"
    echo "请先启动容器: docker-compose up -d mysql"
    exit 1
fi

echo "✅ 远程MySQL容器运行正常"

echo "备份远程数据库..."
docker exec $REMOTE_CONTAINER_NAME mysqldump \
    -u$REMOTE_DB_USER -p$REMOTE_DB_PASSWORD \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    $REMOTE_DB_NAME > $TEMP_DIR/remote_backup_\$(date +%Y%m%d_%H%M%S).sql

echo "恢复数据库..."
docker exec -i $REMOTE_CONTAINER_NAME mysql \
    -u$REMOTE_DB_USER -p$REMOTE_DB_PASSWORD \
    $REMOTE_DB_NAME < $TEMP_DIR/$BACKUP_FILE

if [ \$? -eq 0 ]; then
    echo "✅ 数据库恢复成功"
    echo "清理临时文件..."
    rm -f $TEMP_DIR/$BACKUP_FILE
else
    echo "❌ 错误: 数据库恢复失败"
    exit 1
fi
EOF

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 远程操作失败${NC}"
    rm -f $BACKUP_FILE
    exit 1
fi

# 5. 清理本地临时文件
echo -e "${CYAN}5. 清理本地临时文件...${NC}"
rm -f $BACKUP_FILE
echo -e "${GREEN}✅ 清理完成${NC}"

echo -e "${YELLOW}======================================${NC}"
echo -e "${GREEN}🎉 数据库同步完成!${NC}"
echo -e "${GREEN}本地数据库已成功同步到远程服务器: $REMOTE_SERVER${NC}"
echo -e "${GREEN}远程容器: $REMOTE_CONTAINER_NAME${NC}"
echo -e "${GREEN}数据库: $REMOTE_DB_NAME${NC}"
echo -e "${YELLOW}======================================${NC}"