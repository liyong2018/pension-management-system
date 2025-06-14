# 数据库同步脚本 - 将本地MySQL数据同步到远程服务器
# PowerShell版本
# 使用方法: .\sync_database.ps1

# 设置变量
$REMOTE_SERVER = "root@8.137.85.158"
$LOCAL_DB_HOST = "localhost"
$LOCAL_DB_PORT = "3306"
$LOCAL_DB_NAME = "pension_management_system"
$LOCAL_DB_USER = "root"
$LOCAL_DB_PASSWORD = "Htht1234"

$REMOTE_DB_HOST = "localhost"
$REMOTE_DB_PORT = "13306"
$REMOTE_DB_NAME = "pension_management_system"
$REMOTE_DB_USER = "root"
$REMOTE_DB_PASSWORD = "Htht1234"
$REMOTE_CONTAINER_NAME = "pension-mysql"

# 备份文件名
$BACKUP_FILE = "pension_db_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql"
$TEMP_DIR = "/tmp"

Write-Host "开始数据库同步流程..." -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Yellow

try {
    # 1. 检查本地数据库连接
    Write-Host "1. 检查本地数据库连接..." -ForegroundColor Cyan
    
    # 检查Docker是否运行
    $dockerStatus = docker ps --filter "name=pension-mysql" --format "table {{.Names}}\t{{.Status}}"
    if ($dockerStatus -notmatch "pension-mysql") {
        Write-Host "❌ 错误: 本地MySQL容器未运行" -ForegroundColor Red
        Write-Host "请先启动容器: docker-compose up -d mysql" -ForegroundColor Yellow
        exit 1
    }
    
    # 测试数据库连接
    $testConnection = docker exec pension-mysql mysql -h$LOCAL_DB_HOST -P$LOCAL_DB_PORT -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD -e "SELECT 1" 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ 错误: 无法连接到本地数据库" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 本地数据库连接成功" -ForegroundColor Green
    
    # 2. 导出本地数据库
    Write-Host "2. 导出本地数据库..." -ForegroundColor Cyan
    
    docker exec pension-mysql mysqldump `
        -h$LOCAL_DB_HOST -P$LOCAL_DB_PORT -u$LOCAL_DB_USER -p$LOCAL_DB_PASSWORD `
        --single-transaction `
        --routines `
        --triggers `
        --events `
        --hex-blob `
        --default-character-set=utf8mb4 `
        $LOCAL_DB_NAME > $BACKUP_FILE
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ 错误: 数据库导出失败" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 数据库导出成功: $BACKUP_FILE" -ForegroundColor Green
    
    # 3. 上传备份文件到远程服务器
    Write-Host "3. 上传备份文件到远程服务器..." -ForegroundColor Cyan
    
    scp $BACKUP_FILE "${REMOTE_SERVER}:${TEMP_DIR}/"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ 错误: 文件上传失败" -ForegroundColor Red
        Remove-Item $BACKUP_FILE -ErrorAction SilentlyContinue
        exit 1
    }
    Write-Host "✅ 文件上传成功" -ForegroundColor Green
    
    # 4. 在远程服务器上执行数据库恢复
    Write-Host "4. 在远程服务器上执行数据库恢复..." -ForegroundColor Cyan
    
    $remoteScript = @"
echo "检查远程MySQL容器状态..."
if ! docker ps | grep -q $REMOTE_CONTAINER_NAME; then
    echo "❌ 错误: 远程MySQL容器未运行"
    echo "请先启动容器: docker-compose up -d mysql"
    exit 1
fi

echo "✅ 远程MySQL容器运行正常"

echo "备份远程数据库..."
docker exec `$REMOTE_CONTAINER_NAME mysqldump \
    -u$REMOTE_DB_USER -p$REMOTE_DB_PASSWORD \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    `$REMOTE_DB_NAME > $TEMP_DIR/remote_backup_`$(date +%Y%m%d_%H%M%S).sql

echo "恢复数据库..."
docker exec -i `$REMOTE_CONTAINER_NAME mysql \
    -u$REMOTE_DB_USER -p$REMOTE_DB_PASSWORD \
    `$REMOTE_DB_NAME < $TEMP_DIR/$BACKUP_FILE

if [ `$? -eq 0 ]; then
    echo "✅ 数据库恢复成功"
    echo "清理临时文件..."
    rm -f $TEMP_DIR/$BACKUP_FILE
else
    echo "❌ 错误: 数据库恢复失败"
    exit 1
fi
"@
    
    ssh $REMOTE_SERVER $remoteScript
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ 远程操作失败" -ForegroundColor Red
        Remove-Item $BACKUP_FILE -ErrorAction SilentlyContinue
        exit 1
    }
    
    # 5. 清理本地临时文件
    Write-Host "5. 清理本地临时文件..." -ForegroundColor Cyan
    Remove-Item $BACKUP_FILE -ErrorAction SilentlyContinue
    Write-Host "✅ 清理完成" -ForegroundColor Green
    
    Write-Host "======================================" -ForegroundColor Yellow
    Write-Host "🎉 数据库同步完成!" -ForegroundColor Green
    Write-Host "本地数据库已成功同步到远程服务器: $REMOTE_SERVER" -ForegroundColor Green
    Write-Host "远程容器: $REMOTE_CONTAINER_NAME" -ForegroundColor Green
    Write-Host "数据库: $REMOTE_DB_NAME" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Yellow
    
} catch {
    Write-Host "❌ 脚本执行过程中发生错误: $($_.Exception.Message)" -ForegroundColor Red
    Remove-Item $BACKUP_FILE -ErrorAction SilentlyContinue
    exit 1
}