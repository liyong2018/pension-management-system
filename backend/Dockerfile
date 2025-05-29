# 使用 Maven 和 Java 17 的基础镜像
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和依赖文件
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline

# 复制源代码
COPY src/ ./src/


# 构建应用
RUN mvn clean package -DskipTests

# 使用较小的基础镜像运行应用
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 从构建阶段复制构建好的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 暴露应用端口
EXPOSE 8082

# 设置环境变量
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/pension_management_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=Htht1234

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
