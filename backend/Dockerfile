# 使用Java 17运行环境
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 复制构建好的JAR文件
COPY target/*.jar app.jar

# 暴露应用端口
EXPOSE 8081

# 设置环境变量
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/pension_management_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=Htht1234

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
