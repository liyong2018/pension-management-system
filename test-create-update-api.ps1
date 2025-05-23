# 智能设备模块CRUD操作API测试脚本
$baseUrl = "http://localhost:8081"

Write-Host "=== 智能设备CRUD操作API测试 ===" -ForegroundColor Green

# 测试1: 创建新设备
Write-Host "`n1. 测试创建新设备" -ForegroundColor Cyan
$newDevice = @{
    deviceCode = "TEST001"
    deviceName = "测试设备001"
    deviceType = "健康监测设备"
    deviceBrand = "测试品牌"
    deviceModel = "TEST-V1.0"
    deviceStatus = "在线"
    purchaseDate = "2024-01-20"
    installationLocation = "测试位置"
    elderlyId = 1
    organizationId = 1
    ipAddress = "192.168.1.200"
    macAddress = "AA:BB:CC:DD:EE:FF"
    communicationProtocol = "MQTT"
    dataCollectionFrequency = 120
    alarmThreshold = '{"test": "value"}'
    batteryThreshold = 20
    realTimeData = '{"status": "online"}'
    batteryLevel = 100
    signalStrength = 95
    dataUploadStatus = "正常"
    warrantyExpiryDate = "2025-01-20"
    maintenanceCycle = 90
    nextMaintenanceDate = "2024-04-20"
    failureCount = 0
}

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices" -Method POST -Body ($newDevice | ConvertTo-Json) -ContentType "application/json"
    Write-Host "✓ 创建设备成功" -ForegroundColor Green
    Write-Host "设备ID: $($response.id), 设备编号: $($response.deviceCode)" -ForegroundColor White
    $createdDeviceId = $response.id
} catch {
    Write-Host "✗ 创建设备失败: $($_.Exception.Message)" -ForegroundColor Red
    $createdDeviceId = $null
}

# 测试2: 更新设备状态
if ($createdDeviceId) {
    Write-Host "`n2. 测试更新设备状态" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/$createdDeviceId/status?status=维护中" -Method PUT
        Write-Host "✓ 更新设备状态成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 更新设备状态失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试3: 更新设备电量
if ($createdDeviceId) {
    Write-Host "`n3. 测试更新设备电量" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/$createdDeviceId/battery?batteryLevel=75" -Method PUT
        Write-Host "✓ 更新设备电量成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 更新设备电量失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试4: 更新设备实时数据
if ($createdDeviceId) {
    Write-Host "`n4. 测试更新设备实时数据" -ForegroundColor Cyan
    try {
        $realtimeData = '{"temperature": 25.5, "humidity": 60, "timestamp": "2024-01-23T15:30:00"}'
        Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/$createdDeviceId/realtime-data" -Method PUT -Body $realtimeData -ContentType "application/json"
        Write-Host "✓ 更新设备实时数据成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 更新设备实时数据失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试5: 验证更新结果
if ($createdDeviceId) {
    Write-Host "`n5. 验证设备更新结果" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/$createdDeviceId" -Method GET
        Write-Host "✓ 获取更新后设备详情成功" -ForegroundColor Green
        Write-Host "设备状态: $($response.deviceStatus)" -ForegroundColor White
        Write-Host "电量: $($response.batteryLevel)%" -ForegroundColor White
        Write-Host "实时数据: $($response.realTimeData)" -ForegroundColor White
    } catch {
        Write-Host "✗ 获取设备详情失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试6: 创建告警记录
Write-Host "`n6. 测试创建告警记录" -ForegroundColor Cyan
$newAlarm = @{
    deviceId = if ($createdDeviceId) { $createdDeviceId } else { 1 }
    alarmType = "设备测试"
    alarmLevel = "警告"
    alarmContent = "测试告警记录"
    alarmTime = "2024-01-23T15:30:00"
    alarmData = '{"test_value": 100, "threshold": 80}'
    processStatus = "未处理"
}

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/device-alarms" -Method POST -Body ($newAlarm | ConvertTo-Json) -ContentType "application/json"
    Write-Host "✓ 创建告警记录成功" -ForegroundColor Green
    Write-Host "告警ID: $($response.id), 告警内容: $($response.alarmContent)" -ForegroundColor White
    $createdAlarmId = $response.id
} catch {
    Write-Host "✗ 创建告警记录失败: $($_.Exception.Message)" -ForegroundColor Red
    $createdAlarmId = $null
}

# 测试7: 处理告警
if ($createdAlarmId) {
    Write-Host "`n7. 测试处理告警" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/device-alarms/$createdAlarmId/process?processPerson=测试管理员&processResult=测试处理结果&remarks=API测试" -Method PUT
        Write-Host "✓ 处理告警成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 处理告警失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试8: 删除测试数据
if ($createdDeviceId) {
    Write-Host "`n8. 清理测试数据 - 删除测试设备" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/$createdDeviceId" -Method DELETE
        Write-Host "✓ 删除测试设备成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 删除测试设备失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

if ($createdAlarmId) {
    Write-Host "`n9. 清理测试数据 - 删除测试告警" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/device-alarms/$createdAlarmId" -Method DELETE
        Write-Host "✓ 删除测试告警成功" -ForegroundColor Green
    } catch {
        Write-Host "✗ 删除测试告警失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== CRUD操作API测试完成 ===" -ForegroundColor Green 