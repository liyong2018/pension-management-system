# 养老信息管理系统

本系统基于 Spring Boot（后端）和 Vue 3（前端），实现养老机构、老人档案、智能设备、服务记录、志愿者等全流程信息化管理。

---

## 目录结构

```
pension/
├── backend/                # 后端项目（Spring Boot）
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/pension/
│   │       │   ├── annotation/      # 自定义注解
│   │       │   ├── aspect/         # AOP切面
│   │       │   ├── config/         # 配置类
│   │       │   ├── controller/     # 控制器（REST API）
│   │       │   ├── dao/            # MyBatis DAO接口
│   │       │   ├── dto/            # 数据传输对象
│   │       │   ├── entity/         # 实体类
│   │       │   ├── exception/      # 异常处理
│   │       │   ├── mapper/         # MyBatis映射
│   │       │   ├── model/          # 业务模型
│   │       │   ├── security/       # 安全配置
│   │       │   ├── service/        # 业务服务
│   │       │   └── util/           # 工具类
│   │       └── resources/          # 配置文件、SQL、静态资源
│   ├── docs/                       # 项目文档
│   ├── uploads/                    # 文件上传目录
│   ├── Dockerfile                  # Docker构建文件
│   └── pom.xml                     # Maven配置
│
└── frontend/              # 前端项目（Vue 3）
    ├── public/             # 静态资源
    └── src/
        ├── api/            # API接口封装
        ├── assets/         # 静态资源
        ├── components/     # 公共组件
        ├── constants/      # 常量定义
        ├── router/         # 路由配置
        ├── services/       # 业务服务
        ├── store/          # 状态管理
        ├── utils/          # 工具函数
        ├── views/          # 页面组件
        ├── App.vue         # 根组件
        └── main.js         # 入口文件
```

---

## API接口总览

### 1. 认证与用户管理
- `POST   /api/auth/login`         用户登录，返回token
- `GET    /api/system-users`       获取用户列表
- `POST   /api/system-users`       创建用户
- `PUT    /api/system-users/{id}`  更新用户
- `DELETE /api/system-users/{id}`  删除用户
- `GET    /api/roles`              获取角色列表
- `GET    /api/permissions`        获取权限列表
- `GET    /api/permissions/tree`   获取权限树

### 2. 机构管理
- `GET    /api/organizations`            获取机构列表（分页、搜索）
- `POST   /api/organizations`            创建机构
- `PUT    /api/organizations/{id}`       更新机构
- `DELETE /api/organizations/{id}`       删除机构
- `DELETE /api/organizations/batch`      批量删除机构

### 3. 人员档案管理
- `GET    /api/elderly-profiles`                     获取老人列表（分页、搜索）
- `GET    /api/elderly-profiles/{id}`                获取老人详情
- `POST   /api/elderly-profiles`                     创建老人档案
- `PUT    /api/elderly-profiles/{id}`                更新老人档案
- `DELETE /api/elderly-profiles/{id}`                删除老人档案
- `DELETE /api/elderly-profiles/batch`               批量删除
- `GET    /api/elderly-profiles/{id}/family-members` 获取家属列表
- `POST   /api/elderly-profiles/{id}/family-members` 添加家属
- `PUT    /api/elderly-profiles/{id}/family-members/{fmId}` 更新家属
- `DELETE /api/elderly-profiles/{id}/family-members/{fmId}` 删除家属

### 4. 智能设备与告警
- `GET    /api/smart-devices`                        获取设备列表（分页、搜索）
- `GET    /api/smart-devices/{id}`                   获取设备详情
- `POST   /api/smart-devices`                        创建设备
- `PUT    /api/smart-devices/{id}`                   更新设备
- `DELETE /api/smart-devices/{id}`                   删除设备
- `DELETE /api/smart-devices/batch`                  批量删除
- `GET    /api/smart-devices/elderly/{elderlyId}`    获取指定老人的设备
- `GET    /api/smart-devices/organization/{orgId}`   获取指定机构的设备
- `GET    /api/smart-devices/statistics/type`        设备类型统计
- `GET    /api/smart-devices/statistics/status`      设备状态统计
- `GET    /api/device-alarms`                        获取告警记录（分页、搜索）
- `GET    /api/device-alarms/{id}`                   获取告警详情
- `POST   /api/device-alarms`                        创建告警
- `PUT    /api/device-alarms/{id}`                   更新/处理告警
- `DELETE /api/device-alarms/{id}`                   删除告警
- `DELETE /api/device-alarms/batch`                  批量删除
- `GET    /api/device-alarms/device/{deviceId}`      获取设备的告警
- `GET    /api/device-alarms/statistics/type`        告警类型统计
- `GET    /api/device-alarms/statistics/level`       告警级别统计
- `GET    /api/device-alarms/unprocessed/count`      未处理告警数量

### 5. 服务记录
- `GET    /api/service-records`                      获取服务记录列表（分页、搜索）
- `GET    /api/service-records/{id}`                 获取服务记录详情
- `POST   /api/service-records`                      创建服务记录
- `PUT    /api/service-records/{id}`                 更新服务记录
- `DELETE /api/service-records/{id}`                 删除服务记录
- `DELETE /api/service-records/batch`                批量删除
- `GET    /api/service-records/statistics/status`    服务状态统计
- `GET    /api/service-records/elderly/{elderlyId}`  获取指定老人的服务记录
- `PUT    /api/service-records/{id}/evaluation`      添加服务评价

### 6. 志愿者管理
- `GET    /api/volunteers`                           获取志愿者列表（分页、搜索）
- `GET    /api/volunteers/{id}`                      获取志愿者详情
- `POST   /api/volunteers`                           创建志愿者
- `PUT    /api/volunteers/{id}`                      更新志愿者
- `DELETE /api/volunteers/{id}`                      删除志愿者
- `DELETE /api/volunteers/batch`                     批量删除

### 7. 文件上传与下载
- `POST   /api/files/upload`                         上传文件（multipart/form-data）
- `GET    /api/files/download/{fileId}`              下载文件

### 8. 数据统计与仪表盘
- `GET    /api/dashboard/summary`                    首页统计数据
- `GET    /api/statistics/elderly/age-distribution`  老人年龄分布

---

## 说明
- 所有API均采用RESTful风格，数据格式为JSON。
- 认证接口返回JWT，后续请求需在Header中携带 `Authorization: Bearer {token}`。
- 分页参数统一为 `page`（从0开始）、`size`。
- 详细字段和数据结构请参考后端源码或接口文档。

---

如需详细字段说明、示例数据或二次开发建议，请查阅源码或联系维护者。