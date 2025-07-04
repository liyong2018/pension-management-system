#!/bin/bash

# 数据库同步脚本 - 将远程服务器MySQL数据同步到本地
# 使用方法: ./sync_database_from_remote.sh

# 设置变量
REMOTE_SERVER="root@8.137.85.158"
REMOTE_DB_HOST="localhost"
REMOTE_DB_PORT="13306"
REMOTE_DB_NAME="pension_management_system"
REMOTE_DB_USER="root"
REMOTE_DB_PASSWORD="Htht1234"
REMOTE_CONTAINER_NAME="pension-mysql"

LOCAL_DB_HOST="127.0.0.1"
LOCAL_DB_PORT="3306"
LOCAL_DB_NAME="pension_management_system"
LOCAL_DB_USER="root"
LOCAL_DB_PASSWORD="Htht1234"
LOCAL_CONTAINER_NAME="pension-mysql"

# 备份文件名
BACKUP_FILE="pension_db_remote_backup_$(date +%Y%m%d_%H%M%S).sql"
TEMP_DIR="/tmp"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${GREEN}开始从远程服务器同步数据库...${NC}"
echo -e "${YELLOW}======================================${NC}"

# 1. 检查远程服务器连接并执行所有远程操作
echo -e "${CYAN}1. 连接远程服务器并执行数据库导出...${NC}"

# 在一个SSH会话中完成所有远程操作
ssh $REMOTE_SERVER << EOF
echo "检查SSH连接..."
echo "✅ 远程服务器连接成功"

echo "检查远程MySQL容器状态..."
if ! docker ps | grep -q $REMOTE_CONTAINER_NAME; then
    echo "❌ 错误: 远程MySQL容器未运行"
    echo "请先启动容器: docker-compose up -d mysql"
    exit 1
fi

echo "✅ 远程MySQL容器运行正常"

echo "导出远程数据库..."
docker exec $REMOTE_CONTAINER_NAME mysqldump \\
    -u$REMOTE_DB_USER -p$REMOTE_DB_PASSWORD \\
    --single-transaction \\
    --routines \\
    --triggers \\
    --events \\
    --hex-blob \\
    --default-character-set=utf8mb4 \\
    $REMOTE_DB_NAME > $TEMP_DIR/$BACKUP_FILE

if [ \$? -ne 0 ]; then
    echo "❌ 错误: 远程数据库导出失败"
    exit 1
fi
echo "✅ 远程数据库导出成功: $BACKUP_FILE"
EOF

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 远程操作失败${NC}"
    exit 1
fi

# 2. 下载备份文件到本地
echo -e "${CYAN}2. 下载备份文件到本地...${NC}"

scp $REMOTE_SERVER:$TEMP_DIR/$BACKUP_FILE ./
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 错误: 文件下载失败${NC}"
    # 清理远程临时文件
    ssh $REMOTE_SERVER "rm -f $TEMP_DIR/$BACKUP_FILE"
    exit 1
fi
echo -e "${GREEN}✅ 文件下载成功${NC}"

# 3. 检查本地数据库连接
echo -e "${CYAN}3. 检查本地数据库连接...${NC}"

# 检查本地Docker容器是否运行
if ! docker ps --filter "name=$LOCAL_CONTAINER_NAME" --format "table {{.Names}}\t{{.Status}}" | grep -q "$LOCAL_CONTAINER_NAME"; then
    echo -e "${RED}❌ 错误: 本地MySQL容器未运行${NC}"
    echo -e "${YELLOW}请先启动容器: docker-compose up -d $LOCAL_CONTAINER_NAME${NC}"
    rm -f $BACKUP_FILE
    exit 1
fi

# 测试本地数据库连接
if ! docker exec $LOCAL_CONTAINER_NAME mysql -h$LOCAL_DB_HOST -P$LOCAL_DB_PORT -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD -e "SELECT 1" >/dev/null 2>&1; then
    echo -e "${RED}❌ 错误: 无法连接到本地数据库${NC}"
    rm -f $BACKUP_FILE
    exit 1
fi
echo -e "${GREEN}✅ 本地数据库连接成功${NC}"

# 4. 备份本地数据库
echo -e "${CYAN}4. 备份本地数据库...${NC}"

LOCAL_BACKUP_FILE="local_backup_$(date +%Y%m%d_%H%M%S).sql"
docker exec $LOCAL_CONTAINER_NAME mysqldump \\
    -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD \\
    --single-transaction \\
    --routines \\
    --triggers \\
    --events \\
    $LOCAL_DB_NAME > $LOCAL_BACKUP_FILE

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 本地数据库备份成功: $LOCAL_BACKUP_FILE${NC}"
else
    echo -e "${YELLOW}⚠️  警告: 本地数据库备份失败，但继续执行恢复操作${NC}"
fi

# 5. 恢复数据库到本地
echo -e "${CYAN}5. 恢复数据库到本地...${NC}"

docker exec -i $LOCAL_CONTAINER_NAME mysql \\
    -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD \\
    $LOCAL_DB_NAME < $BACKUP_FILE

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 错误: 数据库恢复失败${NC}"
    echo -e "${YELLOW}如果有本地备份，可以使用以下命令恢复:${NC}"
    if [ -f "$LOCAL_BACKUP_FILE" ]; then
        echo -e "${YELLOW}docker exec -i $LOCAL_CONTAINER_NAME mysql -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD $LOCAL_DB_NAME < $LOCAL_BACKUP_FILE${NC}"
    fi
    rm -f $BACKUP_FILE
    exit 1
fi
echo -e "${GREEN}✅ 数据库恢复成功${NC}"

# 6. 清理临时文件
echo -e "${CYAN}6. 清理临时文件...${NC}"

# 清理本地临时文件
rm -f $BACKUP_FILE
echo -e "${GREEN}✅ 本地临时文件清理完成${NC}"

# 清理远程临时文件
ssh $REMOTE_SERVER "rm -f $TEMP_DIR/$BACKUP_FILE"
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 远程临时文件清理完成${NC}"
else
    echo -e "${YELLOW}⚠️  警告: 远程临时文件清理失败，请手动清理: $REMOTE_SERVER:$TEMP_DIR/$BACKUP_FILE${NC}"
fi

echo -e "${YELLOW}======================================${NC}"
echo -e "${GREEN}🎉 数据库同步完成!${NC}"
echo -e "${GREEN}远程服务器数据已成功同步到本地: $REMOTE_SERVER${NC}"
echo -e "${GREEN}远程容器: $REMOTE_CONTAINER_NAME${NC}"
echo -e "${GREEN}本地容器: $LOCAL_CONTAINER_NAME${NC}"
echo -e "${GREEN}数据库: $LOCAL_DB_NAME${NC}"
if [ -f "$LOCAL_BACKUP_FILE" ]; then
    echo -e "${GREEN}本地备份文件: $LOCAL_BACKUP_FILE${NC}"
fi
echo -e "${YELLOW}======================================${NC}"