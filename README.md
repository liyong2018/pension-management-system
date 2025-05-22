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
  - 机构列表（支持分页和搜索）
  - 机构信息管理
    - 基本信息：名称、简称、类型、地址、联系方式等
    - 运营信息：成立时间、许可证号、床位数、服务人数、收费标准等
    - 人员信息：负责人、员工数量、专业护理人员等
    - 资质信息：消防许可证、卫生许可证、医疗机构执业许可证等
  - 数据展示
    - 分页列表显示
    - 按名称搜索
    - 地址信息合并显示
    - 床位数、服务人数等统计信息
  - 批量操作
    - 批量删除功能
    - 表单验证
      - 必填字段验证
      - 电话号码格式验证
      - 邮箱格式验证
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
  - [x] 查询机构列表（含分页）
  - [x] 更新机构信息
  - [x] 删除机构
  - [x] 批量删除机构
  - [x] 机构管理所有功能已完成
- [ ] 人员档案模块
- [ ] 健康监测模块
- [ ] 服务记录模块
- [ ] 智能设备模块
- [ ] 志愿者管理模块
- [ ] 系统管理模块

### 前端开发
- [x] 项目框架搭建
- [x] 用户认证页面
- [x] 机构管理页面
  - [x] 列表展示
  - [x] 分页功能
  - [x] 搜索功能
  - [x] 新增/编辑表单（已合并为单一address字段）
  - [x] 删除确认
  - [x] 批量操作
  - [x] 机构管理页面所有功能已完成
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

-- 初始化机构数据
INSERT INTO institutions (name, address, contact_person, contact_phone, bed_count, staff_count, establishment_date, license_number, status)
VALUES 
('阳光养老院', '北京市朝阳区阳光路123号', '张三', '13800138001', 100, 30, '2020-01-01', 'L2020001', 1),
('康乐养老中心', '北京市海淀区康乐街45号', '李四', '13800138002', 150, 45, '2019-06-15', 'L2019002', 1),
-- ... 其他测试数据 ...
;
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

## API接口说明

### 机构管理接口
```
GET    /api/organizations        - 获取机构列表（支持分页和搜索）
POST   /api/organizations        - 创建新机构
PUT    /api/organizations/{id}   - 更新机构信息
DELETE /api/organizations/{id}   - 删除机构
DELETE /api/organizations/batch  - 批量删除机构
```

机构数据结构：
```json
{
  "id": "机构ID",
  "name": "机构名称",
  "shortName": "机构简称",
  "type": "机构类型",
  "address": "机构地址",
  "phone": "联系电话",
  "email": "电子邮箱",
  "website": "网址",
  "description": "机构描述",
  "establishmentDate": "成立时间",
  "licenseNumber": "许可证号",
  "businessScope": "经营范围",
  "bedCount": "床位数量",
  "actualServiceCount": "实际服务人数",
  "chargingStandard": "收费标准",
  "area": "机构规模",
  "directorName": "负责人姓名",
  "directorContact": "负责人联系方式",
  "employeeCount": "员工总数",
  "professionalNurseCount": "专业护理人员数量",
  "fireLicense": "消防许可证",
  "sanitaryLicense": "卫生许可证",
  "medicalLicense": "医疗机构执业许可证",
  "otherQualifications": "其他资质证书"
}
```

请求参数示例：
```json
{
  "page": 0,  // 从0开始计数
  "size": 10,
  "sort": "id,asc",
  "name": "搜索关键词"
}
```

## 开发指南

### 目录结构
```
pension/
├── backend/                # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/pension
│   │   │   ├── ├── config/          # 配置类
│   │   │   ├── ├── controller/      # 控制器
│   │   │   ├── ├── dao/            # 数据访问层
│   │   │   ├── ├── dto/            # 数据传输对象
│   │   │   ├── ├── exception/      # 异常处理
│   │   │   ├── ├── mapper/         # MyBatis映射
│   │   │   ├── ├── model/          # 实体类
│   │   │   ├── ├── repository/     # 数据仓库
│   │   │   ├── ├── security/       # 安全相关
│   │   │   ├── ├── service/        # 服务层
│   │   │   ├── ├── util/           # 工具类
│   │   │   └── resources/          # 资源文件
│   └── pom.xml
│
└── frontend/              # 前端项目
    ├── src/
    │   ├── components/    # 公共组件
    │   │   ├── institution/  # 机构相关组件
    │   │   └── common/      # 通用组件
    │   ├── router/        # 路由配置
    │   ├── services/      # API服务
    │   ├── store/         # 状态管理
    │   ├── views/         # 页面视图
    │   ├── APP.vue
    │   ├── main.js
    └── package.json
```

## 已知问题

- Chrome扩展相关的连接错误（调查中）
- 后端API连接错误：
  - 症状：ECONNREFUSED错误
  - 可能原因：
    1. 后端服务未启动
    2. 端口配置不匹配
    3. 代理配置问题
  - 解决方案：
    1. 确保后端服务正常运行
    2. 检查vite.config.js中的代理配置
    3. 验证API路径是否正确（/api/organizations）
- 前端JavaScript类型错误（修复中）
- 内容脚本错误（修复中）

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交改动：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情 