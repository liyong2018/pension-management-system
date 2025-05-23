# 创建设备告警测试数据
$headers = @{ "Content-Type" = "application/json" }

# 为设备ID=8创建告警记录
$alarmData1 = @{
    deviceId = 8
    alarmType = "设备故障"
    alarmLevel = "严重"
    alarmContent = "GPS定位手表电池电量过低，需要及时充电"
    alarmTime = "2024-01-15 14:30:00"
    processStatus = "未处理"
    processPerson = ""
    processTime = ""
    processRemark = ""
} | ConvertTo-Json

$alarmData2 = @{
    deviceId = 8
    alarmType = "网络断连"
    alarmLevel = "警告"
    alarmContent = "设备网络连接中断，无法获取位置信息"
    alarmTime = "2024-01-15 16:45:00"
    processStatus = "处理中"
    processPerson = "李维护"
    processTime = "2024-01-15 17:00:00"
    processRemark = "正在检查网络配置"
} | ConvertTo-Json

$alarmData3 = @{
    deviceId = 8
    alarmType = "低电量"
    alarmLevel = "提醒"
    alarmContent = "设备电量降至20%，建议尽快充电"
    alarmTime = "2024-01-16 09:15:00"
    processStatus = "已处理"
    processPerson = "王技术"
    processTime = "2024-01-16 10:00:00"
    processRemark = "已通知用户充电，问题已解决"
} | ConvertTo-Json

try {
    Write-Host "创建告警记录1..."
    Invoke-RestMethod -Uri "http://localhost:8081/api/device-alarms" -Method Post -Headers $headers -Body $alarmData1
    Write-Host "告警记录1创建成功"
    
    Write-Host "创建告警记录2..."
    Invoke-RestMethod -Uri "http://localhost:8081/api/device-alarms" -Method Post -Headers $headers -Body $alarmData2
    Write-Host "告警记录2创建成功"
    
    Write-Host "创建告警记录3..."
    Invoke-RestMethod -Uri "http://localhost:8081/api/device-alarms" -Method Post -Headers $headers -Body $alarmData3
    Write-Host "告警记录3创建成功"
    
    Write-Host "所有告警记录创建完成！"
} catch {
    Write-Host "创建失败: $_"
} 