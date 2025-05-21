# 养老信息管理系统

这是一个基于Spring Boot和Vue.js的养老信息管理系统。

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL 8.x
- Maven

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router

## 功能特性

- 用户认证与授权
- 机构管理
- 人员管理
- 服务管理
- 数据统计与分析

## 开发进度

### 后端开发
- [x] 项目基础框架搭建
- [x] 数据库设计与创建
- [x] 用户认证相关接口
  - [x] JWT Token 配置
  - [x] 安全配置
  - [x] 用户服务实现
- [x] 机构管理模块
  - [x] 创建机构
  - [x] 查询机构列表
  - [ ] 更新机构信息
  - [ ] 删除机构
- [ ] 人员档案模块
- [ ] 健康监测模块
- [ ] 服务记录模块
- [ ] 智能设备模块
- [ ] 志愿者管理模块
- [ ] 系统管理模块

### 前端开发
- [ ] 项目框架搭建
- [ ] 用户认证页面
- [ ] 机构管理页面
- [ ] 人员档案页面
- [ ] 健康监测页面
- [ ] 服务记录页面
- [ ] 智能设备页面
- [ ] 志愿者管理页面
- [ ] 系统管理页面

## 快速开始

### 后端启动
1. 确保已安装MySQL 8.x
2. 创建数据库：
```sql
CREATE DATABASE pension_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 修改数据库配置（如需要）：
```properties
# backend/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/pension_management_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run serve
```

## 默认账号

系统初始管理员账号：
- 用户名：admin
- 密码：admin123

## 开发指南

### 目录结构
```
pension/
├── backend/                # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   └── pom.xml
│
└── frontend/              # 前端项目
    ├── src/
    │   ├── components/
    │   ├── views/
    │   └── services/
    └── package.json
```

## API文档

启动后端服务后，可以通过以下地址访问API文档：
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI文档: http://localhost:8081/api-docs

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交改动：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情 