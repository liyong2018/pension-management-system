@echo off
chcp 65001 >nul
echo ========================================
echo 微信小程序 TabBar 图标创建工具
echo ========================================
echo.
echo 正在创建临时占位符图标文件...
echo.

REM 创建简单的1x1像素PNG文件作为占位符
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - home.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - home-active.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - service.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - service-active.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - health.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - health-active.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - community.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - community-active.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - profile.png >nul 2>&1
echo iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9jU77zgAAAABJRU5ErkJggg== | certutil -decode -f - profile-active.png >nul 2>&1

echo ✅ 临时图标文件创建完成！
echo.
echo 📋 已创建的文件：
echo    - home.png / home-active.png
echo    - service.png / service-active.png  
echo    - health.png / health-active.png
echo    - community.png / community-active.png
echo    - profile.png / profile-active.png
echo.
echo ⚠️  注意：这些是临时占位符文件
echo    请使用 iconfont 图标库替换为正式图标
echo.
echo 🔗 获取正式图标：
echo    1. 打开 获取图标.html 文件
echo    2. 或访问 https://www.iconfont.cn/
echo    3. 下载 81x81px 的 PNG 格式图标
echo.
pause