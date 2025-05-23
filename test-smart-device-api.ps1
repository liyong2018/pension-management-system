# 智能设备模块API测试脚本
# 基础URL
$baseUrl = "http://localhost:8081"

Write-Host "=== 智能设备模块API测试 ===" -ForegroundColor Green

# 等待服务启动
Write-Host "等待后端服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# 测试1: 获取设备列表（分页）
Write-Host "`n1. 测试获取设备列表（分页）" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices?page=1&size=5" -Method GET
    Write-Host "✓ 获取设备列表成功" -ForegroundColor Green
    Write-Host "总数: $($response.total), 当前页: $($response.pageNum), 页大小: $($response.pageSize)" -ForegroundColor White
    Write-Host "设备数量: $($response.list.Count)" -ForegroundColor White
    if ($response.list.Count -gt 0) {
        Write-Host "第一个设备: $($response.list[0].deviceName) ($($response.list[0].deviceCode))" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 获取设备列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 搜索设备（按设备类型）
Write-Host "`n2. 测试搜索设备（按设备类型）" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/search?deviceType=健康监测设备&page=1&size=10" -Method GET
    Write-Host "✓ 搜索健康监测设备成功" -ForegroundColor Green
    Write-Host "找到 $($response.total) 个健康监测设备" -ForegroundColor White
} catch {
    Write-Host "✗ 搜索设备失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 获取单个设备详情
Write-Host "`n3. 测试获取单个设备详情" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/1" -Method GET
    Write-Host "✓ 获取设备详情成功" -ForegroundColor Green
    Write-Host "设备名称: $($response.deviceName)" -ForegroundColor White
    Write-Host "设备状态: $($response.deviceStatus)" -ForegroundColor White
    Write-Host "电量: $($response.batteryLevel)%" -ForegroundColor White
} catch {
    Write-Host "✗ 获取设备详情失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试4: 获取设备类型统计
Write-Host "`n4. 测试获取设备类型统计" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/statistics/type" -Method GET
    Write-Host "✓ 获取设备类型统计成功" -ForegroundColor Green
    foreach ($key in $response.PSObject.Properties.Name) {
        Write-Host "$key : $($response.$key)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 获取设备类型统计失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试5: 获取设备状态统计
Write-Host "`n5. 测试获取设备状态统计" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/statistics/status" -Method GET
    Write-Host "✓ 获取设备状态统计成功" -ForegroundColor Green
    foreach ($key in $response.PSObject.Properties.Name) {
        Write-Host "$key : $($response.$key)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 获取设备状态统计失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试6: 获取告警记录列表
Write-Host "`n6. 测试获取告警记录列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/device-alarms?page=1&size=5" -Method GET
    Write-Host "✓ 获取告警记录列表成功" -ForegroundColor Green
    Write-Host "总告警数: $($response.total), 当前页: $($response.pageNum)" -ForegroundColor White
    if ($response.list.Count -gt 0) {
        Write-Host "第一个告警: $($response.list[0].alarmContent) ($($response.list[0].alarmLevel))" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 获取告警记录列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试7: 搜索告警记录（按告警类型）
Write-Host "`n7. 测试搜索告警记录（按告警类型）" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/device-alarms/search?alarmType=健康异常&page=1&size=10" -Method GET
    Write-Host "✓ 搜索健康异常告警成功" -ForegroundColor Green
    Write-Host "找到 $($response.total) 个健康异常告警" -ForegroundColor White
} catch {
    Write-Host "✗ 搜索告警记录失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试8: 获取告警类型统计
Write-Host "`n8. 测试获取告警类型统计" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/device-alarms/statistics/type" -Method GET
    Write-Host "✓ 获取告警类型统计成功" -ForegroundColor Green
    foreach ($key in $response.PSObject.Properties.Name) {
        Write-Host "$key : $($response.$key)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 获取告警类型统计失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试9: 获取未处理告警数量
Write-Host "`n9. 测试获取未处理告警数量" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/device-alarms/unprocessed/count" -Method GET
    Write-Host "✓ 获取未处理告警数量成功" -ForegroundColor Green
    Write-Host "未处理告警数量: $response" -ForegroundColor White
} catch {
    Write-Host "✗ 获取未处理告警数量失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试10: 获取指定老人的设备列表
Write-Host "`n10. 测试获取指定老人的设备列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/smart-devices/elderly/1" -Method GET
    Write-Host "✓ 获取老人设备列表成功" -ForegroundColor Green
    Write-Host "老人ID=1的设备数量: $($response.Count)" -ForegroundColor White
    if ($response.Count -gt 0) {
        foreach ($device in $response) {
            Write-Host "- $($device.deviceName) ($($device.deviceStatus))" -ForegroundColor White
        }
    }
} catch {
    Write-Host "✗ 获取老人设备列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== API测试完成 ===" -ForegroundColor Green 