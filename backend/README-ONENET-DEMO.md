# OneNET推送演示应用

这是一个简单的演示应用，用于快速测试OneNET平台的数据推送功能。通过这个应用，您可以验证OneNET平台是否能够成功向您的服务器推送数据，并查看推送的数据内容。

## 功能特点

- 提供URL验证接口，用于OneNET平台的URL验证
- 提供数据接收接口，用于接收OneNET平台推送的设备数据
- 提供健康检查接口，用于检查服务是否正常运行
- 提供Web界面，用于模拟OneNET平台发送请求进行测试

## 快速开始

### Windows环境

1. 双击运行 `start-onenet-demo.bat` 脚本
2. 等待应用启动完成
3. 访问 http://localhost:8081/onenet-demo.html 打开测试界面

### Linux环境

1. 赋予脚本执行权限：`chmod +x start-onenet-demo.sh`
2. 运行脚本：`./start-onenet-demo.sh`
3. 等待应用启动完成
4. 访问 http://localhost:8081/onenet-demo.html 打开测试界面

## 配置说明

应用的配置文件位于 `src/main/resources/application-demo.properties`，您可以根据需要修改以下配置：

- `server.port`：服务器端口，默认为8081
- `onenet.token`：OneNET平台配置的token，用于验证请求来源，为32位数字或英文字母的组合，默认为a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6

## 接口说明

### URL验证接口

- URL：`/api/demo/onenet/push`
- 方法：GET
- 参数：
  - `msg`：推送的消息内容，实例验证阶段为平台生成的随机字符串
  - `nonce`：平台生成的随机字符串
  - `signature`：加密签名，计算方法为：Base64(MD5(token+nonce+msg))
- 返回：验证成功返回msg参数值，验证失败返回错误信息

### 数据接收接口

- URL：`/api/demo/onenet/push`
- 方法：POST
- 参数：
  - `msg`：推送的消息内容，JSON格式的设备数据
  - `nonce`：平台生成的随机字符串
  - `signature`：加密签名，计算方法为：Base64(MD5(token+nonce+msg))
- 返回：处理成功返回"数据接收成功"，处理失败返回错误信息

### 健康检查接口

- URL：`/api/demo/onenet/health`
- 方法：GET
- 返回：服务正常运行时返回"OneNET Push Demo Service is running!"

## 部署到线上环境

### 1. 构建应用

```bash
./mvnw clean package -f pom-onenet-demo.xml -DskipTests
```

### 2. 运行应用

```bash
java -jar target/onenet-push-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
```

### 3. 使用Docker部署（可选）

创建Dockerfile：

```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/onenet-push-demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=demo"]
```

构建并运行Docker镜像：

```bash
docker build -t onenet-push-demo .
docker run -p 8081:8081 onenet-push-demo
```

## 测试应用

1. 访问 http://[您的服务器IP]:8081/onenet-demo.html 打开测试界面
2. 在"URL验证测试"标签页中，填写相关参数并点击"发送验证请求"按钮
3. 在"数据推送测试"标签页中，填写相关参数并点击"发送数据推送"按钮
4. 查看控制台输出，验证数据是否成功接收

## 在OneNET平台配置

1. 登录OneNET平台
2. 创建应用或选择现有应用
3. 配置数据推送：
   - 推送地址：http://[您的服务器IP]:8081/api/demo/onenet/push
   - Token：a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6（或您自定义的32位数字或英文字母组合token）
4. 保存配置并测试连接

## 常见问题

1. **应用无法启动**
   - 检查Java环境是否正确配置
   - 检查端口8081是否被占用

2. **URL验证失败**
   - 检查token配置是否与OneNET平台一致
   - 检查签名计算方法是否正确

3. **无法接收数据**
   - 检查网络连接是否正常
   - 检查防火墙是否开放了8081端口
   - 检查OneNET平台的推送配置是否正确