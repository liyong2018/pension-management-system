#!/bin/bash

echo "正在启动OneNET推送演示应用..."

# 设置Java环境变量（如果需要）
# export JAVA_HOME=/usr/lib/jvm/java-11-openjdk

# 设置应用程序参数
APP_OPTS="--spring.profiles.active=demo"

# 构建应用
echo "正在构建应用..."
./mvnw clean package -f pom-onenet-demo.xml -DskipTests

# 启动应用
echo "正在启动应用..."
java -jar target/onenet-push-demo-0.0.1-SNAPSHOT.jar $APP_OPTS