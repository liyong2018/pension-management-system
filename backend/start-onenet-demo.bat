@echo off
echo 正在启动OneNET推送演示应用...

REM 设置Java环境变量（如果需要）
REM set JAVA_HOME=C:\Program Files\Java\jdk-11

REM 设置应用程序参数
set APP_OPTS=--spring.profiles.active=demo

REM 构建应用
echo 正在构建应用...
call mvnw.cmd clean package -f pom-onenet-demo.xml -DskipTests

REM 启动应用
echo 正在启动应用...
java -jar target\onenet-push-demo-0.0.1-SNAPSHOT.jar %APP_OPTS%

pause