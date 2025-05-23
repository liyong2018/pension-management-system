# 测试创建告警记录API
$jsonData = @'
{
  "deviceId": 12,
  "alarmType": "测试告警",
  "alarmLevel": "警告", 
  "alarmContent": "API测试告警",
  "alarmTime": "2024-01-23T15:30:00",
  "processStatus": "未处理"
}
'@

Write-Host "测试数据:" -ForegroundColor Yellow
Write-Host $jsonData -ForegroundColor White

try {
    Write-Host "`n发送POST请求..." -ForegroundColor Cyan
    $result = Invoke-RestMethod -Uri "http://localhost:8081/api/device-alarms" -Method POST -Body $jsonData -ContentType "application/json"
    
    Write-Host "✓ 创建告警记录成功!" -ForegroundColor Green
    Write-Host "告警ID: $($result.id)" -ForegroundColor White
    Write-Host "告警内容: $($result.alarmContent)" -ForegroundColor White
    Write-Host "创建时间: $($result.createTime)" -ForegroundColor White
} catch {
    Write-Host "✗ 创建告警记录失败!" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody" -ForegroundColor Red
    }
} 