# 养老信息管理系统
这是一个基于Spring Boot和Vue.js的养老信息管理系统。

## 目录结构

### 后端结构 (backend)
```
backend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── pension/
│                       ├── annotation/      # 自定义注解
│                       ├── aspect/         # AOP切面
│                       ├── config/         # 配置类
│                       ├── controller/     # 控制器
│                       ├── dao/            # 数据访问层
│                       ├── dto/            # 数据传输对象
│                       ├── entity/         # 实体类
│                       ├── exception/      # 异常处理
│                       ├── mapper/         # MyBatis映射
│                       ├── model/          # 模型类
│                       ├── security/       # 安全配置
│                       ├── service/        # 服务层
│                       └── util/           # 工具类
├── docs/               # 文档
├── uploads/            # 文件上传目录
├── Dockerfile          # Docker构建文件
└── pom.xml             # Maven配置
```

### 前端结构 (frontend)
```
frontend/
├── public/             # 静态资源
└── src/
    ├── api/            # API接口
    │   ├── dashboard.js        # 仪表板API
    │   ├── deviceAlarm.js      # 设备告警API
    │   ├── dictionary.js      # 数据字典API
    │   ├── elderlyProfile.js  # 老人档案API
    │   └── ...
    ├── assets/         # 静态资源
    ├── components/      # 公共组件
    ├── constants/       # 常量定义
    ├── router/          # 路由配置
    ├── services/        # 服务
    ├── store/           # 状态管理
    ├── utils/           # 工具函数
    ├── views/           # 页面组件
    ├── App.vue          # 根组件
    └── main.js          # 入口文件
```

## API接口文档

### 1. 认证授权

#### 登录
- **URL**: `/api/auth/login`
- **方法**: POST
- **参数**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应**:
  ```json
  {
    "token": "string",
    "username": "string",
    "roles": ["string"]
  }
  ```

### 2. 老人档案管理

#### 获取老人列表
- **URL**: `/api/elderly`
- **方法**: GET
- **参数**: 
  - page: 页码
  - size: 每页数量
  - name: 姓名(可选)
  - community: 社区(可选)

#### 获取老人详情
- **URL**: `/api/elderly/{id}`
- **方法**: GET

### 3. 智能设备管理

#### 获取设备列表
- **URL**: `/api/devices`
- **方法**: GET
- **参数**:
  - type: 设备类型(可选)
  - status: 状态(在线/离线/故障)
  - page: 页码
  - size: 每页数量

#### 创建设备
- **URL**: `/api/devices`
- **方法**: POST
- **请求体**:
  ```json
  {
    "deviceCode": "string",
    "deviceName": "string",
    "deviceType": "HEALTH|SAFETY|ENVIRONMENT",
    "brand": "string",
    "model": "string",
    "status": "ONLINE|OFFLINE|FAULT",
    "installationLocation": "string",
    "purchaseDate": "2025-06-14"
  }
  ```

#### 更新设备信息
- **URL**: `/api/devices/{id}`
- **方法**: PUT
- **请求体**: 同创建设备

#### 删除设备
- **URL**: `/api/devices/{id}`
- **方法**: DELETE

### 4. 设备告警管理

#### 获取告警列表
- **URL**: `/api/device-alarms`
- **方法**: GET
- **参数**:
  - deviceId: 设备ID(可选)
  - alarmType: 告警类型(可选)
  - status: 处理状态(未处理/处理中/已处理)
  - startTime: 开始时间(可选)
  - endTime: 结束时间(可选)
  - page: 页码
  - size: 每页数量

#### 处理告警
- **URL**: `/api/device-alarms/{id}/handle`
- **方法**: PUT
- **请求体**:
  ```json
  {
    "handlerId": "string",
    "handleResult": "string",
    "remark": "string"
  }
  ```

### 5. 服务记录管理

#### 获取服务记录列表
- **URL**: `/api/service-records`
- **方法**: GET
- **参数**:
  - elderId: 老人ID(可选)
  - serviceType: 服务类型(可选)
  - status: 状态(待服务/服务中/已完成/已取消)
  - startTime: 开始时间(可选)
  - endTime: 结束时间(可选)
  - page: 页码
  - size: 每页数量

#### 创建服务记录
- **URL**: `/api/service-records`
- **方法**: POST
- **请求体**:
  ```json
  {
    "elderId": "string",
    "serviceType": "string",
    "serviceContent": "string",
    "serviceTime": "2025-06-14 14:00:00",
    "address": "string",
    "workerId": "string",
    "estimatedDuration": 60,
    "remark": "string"
  }
  ```

### 6. 志愿者管理

#### 获取志愿者列表
- **URL**: `/api/volunteers`
- **方法**: GET
- **参数**:
  - name: 姓名(可选)
  - skill: 技能(可选)
  - status: 状态(活跃/非活跃)
  - page: 页码
  - size: 每页数量

#### 获取志愿者详情
- **URL**: `/api/volunteers/{id}`
- **方法**: GET

#### 创建志愿者
- **URL**: `/api/volunteers`
- **方法**: POST
- **请求体**:
  ```json
  {
    "name": "string",
    "gender": "MALE|FEMALE",
    "idCard": "string",
    "phone": "string",
    "email": "string",
    "address": "string",
    "skills": ["string"],
    "availableTime": "string",
    "status": "ACTIVE|INACTIVE"
  }
  ```

### 7. 数据统计

#### 获取首页统计数据
- **URL**: `/api/dashboard/summary`
- **方法**: GET
- **响应**:
  ```json
  {
    "elderlyCount": 120,
    "institutionCount": 5,
    "workerCount": 45,
    "serviceCount": 356,
    "deviceStatus": {
      "online": 85,
      "offline": 12,
      "fault": 3
    },
    "alarmStats": {
      "unhandled": 5,
      "handling": 2,
      "handled": 28
    }
  }
  ```

#### 获取老人年龄分布
- **URL**: `/api/statistics/elderly/age-distribution`
- **方法**: GET
- **响应**:
  ```json
  [
    {"ageRange": "60-69", "count": 45},
    {"ageRange": "70-79", "count": 52},
    {"ageRange": "80-89", "count": 18},
    {"ageRange": "90+", "count": 5}
  ]
  ```

### 8. 文件上传下载

#### 上传文件
- **URL**: `/api/files/upload`
- **方法**: POST
- **Content-Type**: multipart/form-data
- **参数**:
  - file: 文件
  - type: 文件类型(AVATAR|DOCUMENT|OTHER)
  - relatedId: 关联ID(可选)
- **响应**:
  ```json
  {
    "id": "string",
    "fileName": "string",
    "fileUrl": "string",
    "fileType": "string",
    "fileSize": 1024
  }
  ```

#### 下载文件
- **URL**: `/api/files/download/{fileId}`
- **方法**: GET

## 数据字典

### 1. 设备类型(DeviceType)
- HEALTH: 健康监测设备
- SAFETY: 安全设备
- ENVIRONMENT: 环境监测设备
- OTHER: 其他设备

### 2. 告警级别(AlarmLevel)
- CRITICAL: 严重
- WARNING: 警告
- INFO: 提示

### 3. 服务状态(ServiceStatus)
- PENDING: 待服务
- IN_PROGRESS: 服务中
- COMPLETED: 已完成
- CANCELLED: 已取消

### 4. 志愿者状态(VolunteerStatus)
- ACTIVE: 活跃
- INACTIVE: 非活跃
- SUSPENDED: 暂停

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，请先登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口调用示例

### 1. 登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

### 2. 获取老人列表
```http
GET /api/elderly?page=1&size=10&name=张&community=朝阳区
Authorization: Bearer {token}
```

### 3. 创建设备
```http
POST /api/devices
Authorization: Bearer {token}
Content-Type: application/json

{
  "deviceCode": "DEV20230615001",
  "deviceName": "智能手环",
  "deviceType": "HEALTH",
  "brand": "小米",
  "model": "手环6",
  "status": "ONLINE",
  "installationLocation": "101室",
  "purchaseDate": "2023-06-15"
}
```

## 开发规范

### 1. 接口规范
- 所有API请求都需要在Header中包含`Authorization: Bearer {token}`
- 请求和响应统一使用JSON格式
- 时间格式统一为`yyyy-MM-dd HH:mm:ss`
- 分页参数统一使用`page`和`size`
- 列表查询接口必须支持分页

### 2. 状态码
- 200: 请求成功
- 201: 创建成功
- 204: 删除成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

### 3. 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [],
    "totalElements": 0,
    "totalPages": 0,
    "size": 10,
    "number": 0
  }
}
```

### 4. 统一响应格式
成功响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

失败响应:
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```
  - status: 状态(可选)

#### 创建设备
- **URL**: `/api/devices`
- **方法**: POST
- **请求体**:
  ```json
  {
    "deviceCode": "string",
    "deviceName": "string",
    "deviceType": "string",
    "status": "ONLINE|OFFLINE|FAULT"
  }
  ```

### 4. 服务记录

#### 获取服务记录
- **URL**: `/api/service-records`
- **方法**: GET
- **参数**:
  - elderId: 老人ID(可选)
  - startTime: 开始时间(可选)
  - endTime: 结束时间(可选)

### 5. 系统管理

#### 用户管理
- 获取用户列表: `GET /api/users`
- 创建用户: `POST /api/users`
- 更新用户: `PUT /api/users/{id}`
- 删除用户: `DELETE /api/users/{id}`

#### 角色管理
- 获取角色列表: `GET /api/roles`
- 获取角色权限: `GET /api/roles/{id}/permissions`
- 更新角色权限: `PUT /api/roles/{id}/permissions`

## 部署说明

### 后端部署
1. 安装JDK 11+
2. 安装Maven
3. 配置数据库连接
4. 构建项目:
   ```bash
   cd backend
   mvn clean package
   ```
5. 运行:
   ```bash
   java -jar target/backend-1.0.0.jar
   ```

### 前端部署
1. 安装Node.js 14+
2. 安装依赖:
   ```bash
   cd frontend
   npm install
   ```
3. 开发模式运行:
   ```bash
   npm run dev
   ```
4. 生产构建:
   ```bash
   npm run build
   ```

### Docker部署
```bash
docker-compose up -d
```

## 开发指南

### 开发环境
- 后端: JDK 11+, Maven 3.6+
- 前端: Node.js 14+, npm 6+
- 数据库: MySQL 8.0+

### 代码规范
- 后端: 遵循Java开发规范
- 前端: 遵循Vue.js官方风格指南
- 提交信息: 使用约定式提交规范

## 常见问题

### 1. 数据库连接失败
- 检查`application.yml`中的数据库配置
- 确保MySQL服务已启动
- 检查数据库用户名和密码是否正确

### 2. 前端接口请求失败
- 检查`vite.config.js`中的代理配置
- 确保后端服务已启动
- 检查网络连接

一、首页
正中间：
统计数据：老龄人口、养老机构及设施、从业人员、发放补贴。

 地图信息（中间）：各社区养老机构分布（居家养老、日照、机构）；各社区老人分布（80岁以上、独居、失能、低收入）

老年人分布

左侧：

老人类型统计：正常老人、空巢老人、独居老人、残疾老人、高龄老人、

能力评估：能力完好、轻度失能、中度失能、重度失能、未评估

年龄分布：

右侧：
接入设备数量：SOS报警、烟感、水浸、跌倒、燃气泄露

设备状态：

告警信息：接入智能设备告警数据
 
二、机构管理模块
机构基本信息：
机构名称：养老机构的全称，是机构的重要标识。
机构简称：为了方便使用和记录，对机构名称进行简化的称呼。
机构类型：表明养老机构的性质，如居家养老单位、社区养老单位（日照）、机构养老单位（养老院）
机构地址：详细的机构所在地，包括省、市、区、街道、门牌号等信息。
联系电话：养老机构的固定电话，方便家属和外界联系。
电子邮箱：机构的官方邮箱，可用于接收各类信息和文件。
网址：养老机构的官方网站地址，用于展示机构信息和服务内容
机构运营信息：
成立时间：养老机构正式成立并开始运营的时间。
许可证号：由相关部门颁发的合法运营许可证编号。
经营范围：明确机构提供的养老服务项目，如生活照料、医疗护理、康复服务等。
床位数量：机构内可供老年人居住的床位总数。
实际服务人数：当前实际入住养老机构的老年人数量。
收费标准：不同类型房间、不同服务项目的收费明细。
机构规模：描述机构的占地面积、建筑面积等规模信息。
机构人员信息：
负责人姓名：养老机构的主要负责人姓名。
负责人联系方式：负责人的手机号码、电子邮箱等联系方式。
员工总数：机构内所有工作人员的数量，包括管理人员、护理人员、医护人员、后勤人员等。
专业护理人员数量：具备专业护理资质的工作人员数量。
机构资质信息
消防许可证：表明机构消防设施符合安全标准的许可证。
卫生许可证：机构在食品卫生、环境卫生等方面达到相关标准的许可证明。
医疗机构执业许可证：如果机构内设有医疗机构，需具备此许可证。
其他资质证书：如养老服务质量认证证书、荣誉证书等相关资质证明文件。
 更新controller,dao,dto,mapper,model,service等相关类中的逻辑与字段。
三、人员档案模块
列表：
    人员列表显示内容：姓名、性别、住址、联系电话、所属社区、养老类型。
人员详情：
    基本信息 
姓名：老年人的姓名。
性别：性别信息，男或女。
出生日期：用于记录老年人的出生年月日，以便了解其年龄和提供个性化服务。
身份证号：唯一标识老年人身份的号码，便于信息管理和查询。
联系电话：老年人本人或其紧急联系人的电话号码。
照片：存储老年人的近期照片，方便工作人员识别和管理。
家庭住址：详细的居住地址，方便提供上门服务和紧急情况下的定位。
所属社区：居住的社区（下拉选择）。
养老类型：居家养老、社区养老（日照）、机构养老（养老院）。
     健康信息
过往病史：记录老年人曾经患过的重大疾病、慢性疾病等，帮助医护人员了解其健康状况和制定相应的护理计划。
过敏史：包括药物过敏、食物过敏等信息，避免在治疗和饮食安排中出现过敏反应。
体检报告：可关联定期体检的报告文件或记录关键体检指标，如血压、血糖、血脂、心电图等结果，以便跟踪健康变化。
当前健康状况：简要描述老年人当前的身体状况，如是否患有慢性病、身体机能是否有障碍等。
护理等级：根据老年人的健康状况和生活自理能力确定的护理级别，如一级护理、二级护理等，指导护理人员提供相应级别的服务。
能力评估：能力完好、轻度失能、中度失能、重度失能
    生活信息
生活习惯：包括饮食偏好、睡眠习惯、日常活动等信息，有助于为老年人提供符合其生活习惯的服务和环境。
兴趣爱好：了解老年人的兴趣爱好，如绘画、书法、音乐等，以便组织适合的娱乐活动，丰富其精神生活。
宗教信仰：尊重老年人的宗教信仰，在必要时提供相应的宗教服务或支持。
    家属信息
家属姓名：老年人直系亲属或主要联系人的姓名。
与老人关系：说明家属与老年人的关系，如子女、配偶、父母等。
联系电话：家属的电话号码，方便在紧急情况下及时联系。
    服务信息
服务记录：记录老年人接受过的各类服务，包括服务时间、服务内容、服务人员等信息，便于跟踪服务历史和评估服务质量。
费用信息：涉及老年人在养老机构或使用养老服务过程中的费用缴纳情况，如床位费、护理费、餐饮费等明细，以及缴费记录和欠费情况。
    其他信息
备注：用于记录其他重要信息或特殊情况，如老年人的特殊需求、特殊经历等，以便工作人员全面了解老年人的情况。
 
 四、智能设备模块
智能设备基本信息：
设备编号：每个智能设备的唯一标识符，便于系统识别和管理。
设备名称：设备的商品名称或型号，如"智能手环V2.0"、"血压监测仪Pro"等。
设备类型：设备的分类，如健康监测设备（手环、血压计、血糖仪等）、智能家居设备（灯光控制器、温控系统、智能门锁等）、安全设备（烟感器、水浸传感器、SOS报警器等）、定位设备（GPS定位器、室内定位设备等）。
设备品牌：设备制造商的品牌信息。
设备型号：具体的产品型号，用于技术支持和维护。
设备状态：设备当前的运行状态，如在线、离线、故障、维护中等。
购买日期：设备的采购时间，用于保修期计算和资产管理。
安装位置：设备的具体安装地点，如房间号、楼层、具体位置描述等。
绑定用户：与设备关联的老年人信息，记录设备的使用对象。
设备配置信息：
网络配置：设备的网络连接信息，如IP地址、MAC地址、Wi-Fi配置等。
通信协议：设备支持的通信协议，如TCP/IP、MQTT、LoRa等。
数据采集频率：设备采集数据的时间间隔设置。
报警阈值：各类监测数据的报警临界值，如心率异常范围、血压异常值等。
电量阈值：设备低电量报警的电量百分比设置。
设备监控信息：
实时数据：设备当前采集的各类数据，如温度、湿度、心率、血压等。
电量状态：设备当前的电量百分比和充电状态。
信号强度：设备的网络连接信号强度。
最后通信时间：设备最后一次与系统成功通信的时间。
数据上传状态：设备数据上传的状态，如正常、异常、延迟等。
设备维护信息：
保修期限：设备的保修截止日期。
维护周期：设备定期维护的时间间隔。
下次维护时间：计划的下一次维护日期。
维修记录：设备的历史维修记录，包括维修时间、故障原因、维修内容、维修人员等。
保养记录：设备的日常保养记录，包括保养时间、保养内容、保养人员等。
故障统计：设备的故障次数和故障类型统计。

智能设备告警记录：
告警ID：每条告警记录的唯一标识符。
设备ID：产生告警的设备标识，关联智能设备表。
告警类型：告警的分类，如设备故障、数据异常、低电量、网络断连、健康异常等。
告警级别：告警的严重程度，如严重、警告、提醒等。
告警内容：具体的告警信息描述。
告警时间：告警产生的时间。
告警数据：触发告警的具体数据值。
处理状态：告警的处理状态，如未处理、处理中、已处理、已忽略等。
处理人员：负责处理告警的工作人员。
处理时间：告警处理的时间。
处理结果：告警处理的结果描述。
备注：告警处理的补充说明。

设备数据采集管理：
健康数据采集：心率、血压、血糖、体温、睡眠质量、运动量等生理指标数据。
环境数据采集：室内温度、湿度、空气质量、光照强度等环境参数。
安全数据采集：烟雾浓度、燃气浓度、水浸检测、门窗状态等安全监测数据。
行为数据采集：活动轨迹、生活习惯、设备使用频率等行为分析数据。
数据存储与分析：对采集的数据进行存储、清洗、分析，为健康监测和预警提供数据支撑。

设备用户绑定管理：
用户设备关联：记录老年人与智能设备的绑定关系，一个老人可以绑定多个设备。
家属授权：允许家属查看关联老人的设备数据和告警信息。
权限管理：设置不同用户对设备数据的访问权限。
绑定历史：记录设备绑定关系的变更历史。

设备资源管理：
设备库存：管理机构内智能设备的库存情况，包括在用、备用、维修中、报废等状态。
设备分配：根据老年人的需求和设备可用性进行设备分配。
设备回收：管理设备的回收、重新分配或报废流程。
成本统计：统计设备采购、维护、更换等相关成本。
 
五、健康监测
实时反馈
在平台上实时反馈老年人的健康监测数据，让老年人和家属随时了解健康状况。
 预警提醒：
异常报警数据提醒，通过声音、震动或推送通知，提醒老年人注意健康问题。
家属参与：
家属可以通过Web平台或APP查看老年人的健康数据，参与健康管理。

六、服务记录
服务列表：显示人员姓名、服务地址、服务内容、工单价格。
详情：参考服务记录表
 
七、志愿者管理

志愿者信息管理
包括志愿者的基本信息（如姓名、年龄、联系方式、技能特长等）、注册登记、身份认证等功能，便于对志愿者进行统一管理和查询。
● 姓名：志愿者的真实姓名，用于身份识别和沟通交流。
● 性别：有助于了解志愿者的性别分布，以便在安排服务时考虑性别因素。
● 年龄：反映志愿者的年龄层次，可根据不同年龄段的特点和优势分配合适的服务任务。
● 身份证号码：唯一标识志愿者身份，用于注册登记、身份验证等，确保信息的准确性和安全性。
● 联系方式：包括手机号码、电子邮箱等，方便平台与志愿者及时取得联系，发送服务通知、培训信息等。
● 常住地址：了解志愿者的居住地点，以便在分配服务任务时考虑距离因素，尽量安排就近服务，提高服务效率。
● 户籍地址：作为辅助信息，可用于一些统计分析或在特定情况下核实志愿者的身份背景。
● 最高学历：体现志愿者的知识水平和学习经历，对于一些需要专业知识的服务项目，如文化教育、心理咨询等，可根据学历筛选合适的志愿者。
● 毕业院校：了解志愿者的毕业院校，在一定程度上可以反映其专业背景和综合素质。
● 职业：明确志愿者的职业身份，有助于根据其职业技能和经验安排相关的服务工作，例如医护人员可参与健康护理服务，教师可参与文化教育活动等。
● 工作单位：记录志愿者的工作单位信息，以便在需要时与志愿者的工作单位进行沟通协调，同时也可作为参考信息了解志愿者的工作环境和时间安排，合理安排服务任务。
● 专业技能：如医疗护理技能、康复训练技能、心理咨询技能、计算机操作技能等，这些专业技能对于提供特定类型的养老服务非常重要。
● 兴趣爱好：例如音乐、舞蹈、绘画、书法、摄影等，可根据志愿者的兴趣爱好组织相应的文化娱乐活动，丰富老人的精神生活。
● 语言能力：如果志愿者掌握多种语言，如英语、日语、韩语等，在一些涉外养老服务或与有语言需求的老人交流时能发挥作用。
● 服务意向：了解志愿者希望参与的养老服务领域，如生活照料、陪伴关爱、健康管理、文化活动组织等，以便为其匹配合适的服务项目。
● 服务时间：包括志愿者能够提供服务的时间段，如每周一、三、五上午，或者每天下午等，以及预计的服务时长，这是安排服务任务的重要依据。
● 服务经历：记录志愿者以往的志愿服务经历，包括服务的项目、时间、地点、获得的评价等，有助于评估志愿者的服务能力和经验。
● 联系人姓名：填写志愿者在紧急情况下的联系人姓名。
● 与志愿者关系：说明紧急联系人与志愿者的关系，如亲属、朋友等。
● 联系电话：确保在志愿者遇到突发情况或无法正常参与服务时，平台能够及时联系到其紧急联系人，以便采取相应措施。

服务项目管理
发布各类养老服务项目，如生活照料、陪伴聊天、康复护理、文化娱乐等，明确服务内容、时间、地点和要求等信息，方便志愿者选择参与。

服务匹配与派遣
根据志愿者的技能、时间和老人的需求，通过智能算法进行精准匹配，将合适的志愿者派遣到相应的服务项目中，提高服务效率和质量。

服务记录与评价
志愿者在完成服务后，记录服务的时间、内容、效果等信息，同时老人或其家属可以对志愿者的服务进行评价，为后续的考核和激励提供依据。

培训管理
提供线上或线下的志愿者培训课程，包括养老服务知识、沟通技巧、应急处理等方面的培训，提升志愿者的服务能力和专业素养。

激励与积分管理
建立志愿者激励机制，通过积分系统对志愿者的服务进行量化统计，志愿者可凭借积分兑换礼品、获得荣誉证书或享受其他优惠政策，激发志愿者的积极性和持续性。

八、系统管理
账户、角色、权限、菜单、日志等管理，与后台共用。

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
- 智能设备管理
  - 设备列表（支持分页和搜索）
  - 设备信息管理
    - 基本信息：设备编号、名称、类型、品牌、型号、状态等
    - 配置信息：网络配置、通信协议、数据采集频率、报警阈值等
    - 监控信息：实时数据、电量状态、信号强度、通信状态等
    - 维护信息：保修期限、维护周期、维修记录、保养记录等
  - 告警管理
    - 告警记录列表（分页显示）
    - 告警类型分类（设备故障、数据异常、低电量等）
    - 告警处理状态跟踪
    - 告警统计分析
  - 用户绑定
    - 设备与老人关联管理
    - 家属授权设置
    - 绑定历史记录
  - 数据采集
    - 健康数据收集（心率、血压、血糖等）
    - 环境数据监测（温度、湿度、空气质量等）
    - 安全数据检测（烟雾、燃气、水浸等）
  - 资源管理
    - 设备库存管理
    - 设备分配与回收
    - 成本统计分析
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
- [x] 人员档案模块
  - [x] 创建人员档案
  - [x] 查询人员档案列表（含分页、搜索）
  - [x] 获取人员档案详情
  - [x] 更新人员档案
  - [x] 删除人员档案
  - [x] 批量删除人员档案
  - [x] 人员档案家属信息管理 (增删改查)
- [x] 智能设备模块
  - [x] 智能设备信息管理 (增删改查)
  - [x] 设备列表（支持分页和搜索）
  - [x] 设备状态管理和统计
  - [x] 设备告警记录管理
  - [x] 告警历史查询和处理
  - [x] 告警详情查看和处理流程
  - [x] 设备用户绑定管理
  - [x] 批量操作和表单验证
- [x] 服务记录模块
  - [x] 创建服务记录
  - [x] 查询服务记录列表（含分页、搜索、筛选）
  - [x] 获取服务记录详情
  - [x] 更新服务记录信息
  - [x] 删除服务记录
  - [x] 批量删除服务记录
  - [x] 服务状态统计
  - [x] 服务评价系统
  - [x] 时间范围查询
  - [x] 与老人档案数据集成
- [ ] 健康监测模块
- [x] 志愿者管理模块
  - [x] 志愿者信息管理 (增删改查)
  - [x] 志愿者列表（支持分页和搜索）
  - [x] 志愿者状态管理和统计
  - [x] 服务项目匹配和派遣
  - [x] 服务记录与评价系统
  - [x] 培训管理和积分系统
- [x] 系统管理模块
  - [x] 用户管理 - 系统用户账号管理（完整CRUD功能）
  - [x] 角色管理 - 角色权限配置（完整RBAC实现）
  - [x] 权限管理 - 菜单功能权限（完整权限树结构）
  - [x] 菜单管理 - 系统菜单结构管理（层级菜单支持）
  - [x] 权限分配 - 用户角色权限关联管理
  - [x] 系统管理API - 完整的REST API接口
  - [x] MapStruct映射 - 自动对象映射和DTO转换
  - [x] 数据库优化 - 完整的外键关系和测试数据

### 前端开发
- [x] 项目框架搭建
- [x] 用户认证页面
- [x] 首页仪表板（最新优化）
  - [x] 智慧养老监控面板设计
  - [x] 地图为主体的布局架构
  - [x] 统计数据卡片（老龄人口、养老机构、从业人员、发放补贴）
  - [x] 老人类型分析饼图
  - [x] 能力评估进度条
  - [x] SOS设备在线率环形图
  - [x] 未处理告警列表（自适应高度）
  - [x] 地图图层控制（社区、机构、告警、重置）
  - [x] OpenStreetMap地图集成
  - [x] 多类型标记和弹出信息卡
  - [x] 响应式布局适配
  - [x] 实时数据展示
- [x] 机构管理页面
  - [x] 列表展示
  - [x] 分页功能
  - [x] 搜索功能
  - [x] 新增/编辑表单（已合并为单一address字段）
  - [x] 删除确认
  - [x] 批量操作
  - [x] 机构管理页面所有功能已完成
- [x] 人员档案页面
  - [x] 列表展示（含分页、搜索）
  - [x] 新增/编辑/查看对话框
  - [x] 删除及批量删除功能
  - [x] 表单校验
  - [x] 家属信息管理
- [x] 智能设备页面
  - [x] 设备列表展示和管理
  - [x] 设备信息新增/编辑/查看
  - [x] 设备状态统计卡片
  - [x] 告警历史管理对话框
  - [x] 告警详情查看/新增/处理
  - [x] 设备筛选和搜索功能
  - [x] 批量操作和状态管理
- [x] 服务记录页面
  - [x] 列表展示（含分页、搜索、筛选）
  - [x] 新增/编辑/查看对话框
  - [x] 删除及批量删除功能
  - [x] 表单校验和样式优化
  - [x] 服务评价对话框
  - [x] 状态统计卡片
  - [x] 时间范围筛选
  - [x] 下拉框宽度优化
- [ ] 健康监测页面
- [x] 志愿者管理页面
  - [x] 志愿者列表展示（含分页、搜索）
  - [x] 新增/编辑/查看志愿者对话框
  - [x] 删除及批量删除功能
  - [x] 志愿者状态统计卡片
  - [x] 表单校验和样式优化
- [x] 系统管理页面
  - [x] 系统管理主页 - 包含管理导航、统计数据、快捷操作
  - [x] 用户管理页面 - 完整的用户CRUD功能，含搜索分页
  - [x] 角色管理页面 - 角色权限配置界面（占位页面）
  - [x] 权限管理页面 - 菜单权限管理界面（占位页面）
  - [x] 路由配置 - 系统管理相关路由完整配置
  - [x] 入口集成 - 在`VolunteerListSimple.vue`中添加系统管理快捷入口

### 用户体验与UI
- [x] 顶部用户下拉菜单
  - [x] 退出登录功能
  - [x] 修改密码（模态框）
  - [x] 个人中心（模态框，可修改姓名、邮箱、手机号）

### 系统集成与测试
- [x] 后端服务部署
  - [x] 项目在8080端口正常运行
  - [x] 所有API接口测试通过
  - [x] 数据库连接正常
  - [x] 首页仪表板API集成
- [x] 前端服务部署
  - [x] 项目在3000端口正常运行
  - [x] 构建无错误，所有页面正常访问
  - [x] API调用正常
  - [x] 首页仪表板功能完整
- [x] 数据库初始化
  - [x] 完整的数据库脚本
  - [x] 测试数据创建
  - [x] 外键关系正确
  - [x] 机构表坐标字段添加
- [x] 功能验证
  - [x] 用户角色权限测试通过
  - [x] 权限分配功能正常
  - [x] 权限树形结构正常显示
  - [x] 角色权限关联查询正常
  - [x] 系统管理入口集成成功
  - [x] 首页仪表板数据展示正常
- [x] 首页仪表板系统
  - [x] 数据统计API集成（老龄人口：23人，养老机构：3个，从业人员：2人，发放补贴：¥258.0万）
  - [x] 地图数据API（社区：11个，机构：3个，告警：2条，老人档案：23个）
  - [x] 图表组件集成（ECharts饼图、环形图、自定义进度条）
  - [x] 地图功能完善（Leaflet地图、图层切换、自定义标记、弹出信息卡）
  - [x] 响应式布局优化（避免菜单遮挡、图例独立显示、告警面板自适应）

### 最新完成功能（2024年12月更新）

#### 首页仪表板优化（2024年12月27日最新）
**优化内容**：
- ✅ **首页仪表板重新设计** - 以地图为主体的智慧养老监控面板
- ✅ **顶部数据统计** - 老龄人口、养老机构、从业人员、发放补贴四大核心指标
- ✅ **左侧数据面板** - 老人类型分析（饼图）+ 能力评估（进度条）
- ✅ **右侧监控面板** - SOS设备在线率（环形图）+ 未处理告警列表（自动高度）
- ✅ **底部控制栏** - 地图图层控制（社区、机构、告警、重置）
- ✅ **独立图例面板** - 左侧垂直排列的图例，避免与主菜单冲突
- ✅ **响应式布局** - 适配不同屏幕尺寸，移动端友好
- ✅ **实时数据集成** - 连接后端API，显示真实的统计数据和地图信息

**地图功能**：
- ✅ **OpenStreetMap地图** - 高清地图背景，以北京市为中心
- ✅ **多图层支持** - 社区分布、养老机构、告警位置可独立切换
- ✅ **智能标记** - 不同类型机构使用不同颜色和图标标识
- ✅ **弹出信息卡** - 点击标记显示详细信息，包含渐变背景和图标
- ✅ **动态告警** - 告警标记带有脉冲动画效果，突出显示紧急情况
- ✅ **坐标数据真实化** - 为机构表添加经纬度字段，使用真实的北京市坐标

**布局优化**：
- ✅ **避免菜单遮挡** - 调整左侧面板位置，为主菜单预留空间
- ✅ **图例独立显示** - 将图例从底部控制栏独立出来，垂直排列在最左侧
- ✅ **告警面板自适应** - 未处理告警面板高度自动扩展到距离底边5px
- ✅ **紧凑型设计** - 优化组件间距和尺寸，提高信息密度

#### 前端布局优化（2024年12月最新）
**优化内容**：
- ✅ **顶部菜单布局优化** - 移除侧边栏，将菜单横向排列在顶部
- ✅ **标题位置调整** - "养老信息管理系统"标题显示在左侧
- ✅ **菜单居中显示** - 菜单项在顶部导航栏中居中对齐
- ✅ **智能设备子菜单** - 包含"设备管理"和"告警管理"两个子菜单项
- ✅ **左侧手风琴菜单** - 将顶部菜单改为左侧手风琴样式，提升用户体验
- ✅ **侧边栏折叠功能** - 默认收起状态，点击按钮展开/收起
- ✅ **手风琴互斥展开** - 只能展开一个主菜单，其他自动收起
- ✅ **点击外部自动收起** - 点击主内容区域或遮罩层自动收起菜单

**技术实现**：
- ✅ 修改 `frontend/src/App.vue` 布局结构
- ✅ 使用 `el-menu` 的 `mode="horizontal"` 实现横向菜单（已改为垂直菜单）
- ✅ 使用 `el-aside` 和 `el-container` 重新设计布局结构
- ✅ CSS Flexbox布局：`justify-content: space-between` 和 `justify-content: center`
- ✅ 响应式设计，支持不同屏幕尺寸
- ✅ 保持原有的菜单功能和路由跳转
- ✅ 手风琴样式菜单，支持子菜单展开收起
- ✅ 优化菜单样式和交互效果，添加悬停和激活状态
- ✅ 自定义滚动条样式，提升视觉体验
- ✅ 使用 `:collapse="isCollapsed"` 实现菜单折叠
- ✅ 使用 `:unique-opened="true"` 实现手风琴互斥展开
- ✅ 添加折叠按钮和遮罩层，支持点击外部收起
- ✅ 平滑过渡动画，提升用户体验

**菜单结构**：
- ✅ 首页、机构管理、人员档案、智能设备、服务记录、志愿者管理、系统管理
- ✅ 智能设备下级菜单：设备管理、告警管理
- ✅ 系统管理下级菜单：用户管理、角色管理、权限管理、菜单管理、日志管理、字典管理
- ✅ 左侧边栏宽度250px（展开）/64px（收起），深色主题配色
- ✅ 顶部头部区域用于放置用户信息和操作按钮
- ✅ 默认收起状态，节省屏幕空间
- ✅ 移动端适配，支持遮罩层和触摸操作

#### 系统管理模块（已完成）
**后端实现**：
- ✅ `MenuPermissionService` - 权限管理业务逻辑接口
- ✅ `MenuPermissionDao` - 数据访问层接口  
- ✅ `MenuPermissionDao.xml` - MyBatis XML映射文件
- ✅ `MenuPermissionServiceImpl` - 业务逻辑实现类
- ✅ `MenuPermissionController` - REST API控制器
- ✅ MapStruct编译问题解决，重新生成实现类

**API接口（已测试）**：
- ✅ `GET /api/system-users` - 用户管理API
- ✅ `GET /api/roles` - 角色管理API  
- ✅ `GET /api/permissions` - 权限管理API
- ✅ `GET /api/permissions/tree` - 权限树形结构API
- ✅ `GET /api/permissions/role/{roleId}` - 根据角色查询权限
- ✅ `GET /api/permissions/user/{userId}` - 根据用户查询权限

**前端实现**：
- ✅ 路由配置 - 系统管理相关路由
- ✅ `SystemManagement.vue` - 系统管理主页
- ✅ `SystemUserList.vue` - 完整的用户管理页面
- ✅ `RoleList.vue` - 角色管理占位页面
- ✅ `PermissionList.vue` - 权限管理占位页面
- ✅ 入口集成 - 在`VolunteerListSimple.vue`中添加系统管理快捷入口

**测试数据**：
- ✅ 权限数据："System Management"(ID:1), "User Management"(ID:2)
- ✅ 角色权限分配：超级管理员分配权限ID 1和2
- ✅ 用户角色分配：admin用户分配超级管理员角色

**技术特点**：
- ✅ 完整的RBAC权限模型（用户-角色-权限）
- ✅ 树形权限结构支持菜单层级关系
- ✅ MyBatis XML映射文件实现数据访问
- ✅ 分页和搜索功能
- ✅ MapStruct自动对象映射

#### 用户管理页面优化（2024年12月最新）
**优化内容**：
- ✅ **删除ID列** - 移除表格中的ID列，优化页面布局
- ✅ **真实统计数据** - 使用基于API的真实用户统计数据
- ✅ **统计数据实现**：
  - 总用户数：6个用户
  - 活跃用户：5个用户（isActive=true）
  - 管理员用户：1个用户（isAdmin=true）
  - 今日登录：基于操作日志的真实统计

**技术实现**：
- ✅ 新增 `loadUserStats()` 函数获取真实统计数据
- ✅ 调用 `/api/system-users?page=1&size=1000` 获取所有用户
- ✅ 调用 `/api/operation-logs` 获取今日登录统计
- ✅ 实时计算统计指标，确保数据准确性
- ✅ 完善的错误处理和降级方案

#### 角色管理页面优化（2024年12月最新）
**优化内容**：
- ✅ **隐藏ID列** - 移除表格中的ID列，优化页面布局
- ✅ **真实用户数统计** - 显示实际分配给每个角色的用户数量
- ✅ **统计数据验证**：
  - 机构负责人：5个用户
  - 超级管理员：1个用户
  - 机构管理员：0个用户
  - 普通用户：0个用户

**技术实现**：
- ✅ 新增 `loadRoleUserCounts(roleList)` 函数统计角色用户数
- ✅ 调用 `/api/system-users?page=1&size=1000` 获取所有用户数据
- ✅ 遍历用户数据，统计每个角色的分配用户数量
- ✅ 支持多种用户角色数据格式的兼容性处理
- ✅ 更新统计卡片中的"已分配用户"数据

**数据准确性保证**：
- ✅ 基于真实的用户-角色关联数据
- ✅ 支持 `user.roles` 数组格式
- ✅ 支持 `user.roleIds` 数组格式  
- ✅ 基于 `user.isAdmin` 的推断逻辑
- ✅ 完整的错误处理和降级方案

#### 首页数据修复和代码清理（2024年12月29日最新）
**问题解决**：
- ✅ **首页数据问题排查** - 发现后端服务停止导致前端使用模拟数据
- ✅ **端口配置修复** - 解决8080端口冲突，改为8085端口统一配置
- ✅ **后端服务恢复** - 成功启动后端服务，API返回真实数据库数据
- ✅ **Dashboard API调试** - 验证数据统计接口正常返回真实数据：
  - 老龄人口：23人，养老机构：3个，从业人员：3人
  - 发放补贴：¥258.0万，社区数据：11个，告警数据：2条

**代码清理优化**：
- ✅ **删除调试代码** - 移除DashboardService中的System.out.println调试语句
- ✅ **简化异常处理** - 清理冗余的异常打印信息，保持核心业务逻辑
- ✅ **移除临时端点** - 删除`/auth/generate-hash`临时密码生成端点
- ✅ **安全配置更新** - 移除对已删除端点的安全配置引用
- ✅ **代码规范化** - 优化循环结构和错误处理逻辑

**服务部署状态**：
- ✅ **后端服务** - 在8085端口稳定运行，所有API接口正常
- ✅ **数据库连接** - MySQL连接正常，真实数据查询无误
- ✅ **前端集成** - vite.config.js代理配置正确指向8085端口
- ✅ **首页展示** - 显示真实统计数据，不再依赖模拟数据降级方案

**技术改进**：
- ✅ **错误处理优化** - 简化异常捕获，保留必要的错误追踪
- ✅ **代码可维护性** - 移除开发调试代码，提高生产环境代码质量
- ✅ **安全性提升** - 删除不必要的临时测试端点
- ✅ **性能优化** - 减少无用的日志输出，提升系统响应速度

## 环境要求

### 开发环境
- **Java**: JDK 17 或更高版本
- **Node.js**: 16.x 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6 或更高版本
- **Git**: 用于版本控制

### 推荐IDE
- **后端**: IntelliJ IDEA 或 Eclipse
- **前端**: VS Code 或 WebStorm
- **数据库**: MySQL Workbench 或 Navicat

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd pension-management-system
```

### 2. 数据库配置
1. 确保已安装MySQL 8.x
2. 创建数据库：
```sql
CREATE DATABASE pension_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 执行数据库初始化脚本：
```bash
# 在项目根目录执行
mysql -u root -p pension_management_system < database/init.sql
mysql -u root -p pension_management_system < database/test_data.sql
```

4. 修改数据库配置：
```properties
# backend/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/pension_management_system
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8085` 启动

### 4. 前端启动
```bash
cd frontend
npm install
npm run serve
```

前端服务将在 `http://localhost:3000` 启动

### 5. 访问系统
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8085
- API文档: http://localhost:8085/swagger-ui.html

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

### 人员档案模块API

```
GET    /api/elderly-profiles                     - 获取人员列表（支持分页和搜索）
GET    /api/elderly-profiles/{id}                - 获取单个人员详情
POST   /api/elderly-profiles                     - 创建新人员档案
PUT    /api/elderly-profiles/{id}                - 更新人员档案信息
DELETE /api/elderly-profiles/{id}                - 删除人员档案
DELETE /api/elderly-profiles/batch               - 批量删除人员档案

GET    /api/elderly-profiles/{id}/family-members        - 获取人员家属列表
POST   /api/elderly-profiles/{id}/family-members        - 添加人员家属
PUT    /api/elderly-profiles/{id}/family-members/{fmId} - 更新人员家属信息
DELETE /api/elderly-profiles/{id}/family-members/{fmId} - 删除人员家属
```

人员档案数据结构：
```json
{
  "id": "老人ID",
  "name": "姓名",
  "gender": "性别",
  "birthDate": "出生日期",
  "idCardNumber": "身份证号",
  "phone": "联系电话",
  "photoUrl": "照片URL",
  "addressDetail": "家庭住址",
  "community": "所属社区",
  "pensionType": "养老类型",
  "medicalHistory": "过往病史",
  "allergyHistory": "过敏史",
  "physicalExamReport": "体检报告",
  "currentHealthStatus": "当前健康状况",
  "careLevel": "护理等级",
  "abilityAssessment": "能力评估",
  "livingHabits": "生活习惯",
  "hobbies": "兴趣爱好",
  "religiousBelief": "宗教信仰",
  "remarks": "备注",
  "organizationId": "所属机构ID（如果是机构养老）"
}
```

家属信息数据结构：
```json
{
  "id": "家属ID",
  "elderlyId": "老人ID",
  "name": "家属姓名",
  "relationship": "与老人关系",
  "phone": "联系电话"
}
```

分页查询参数：
```json
{
  "page": 0,
  "size": 10,
  "sort": "id,asc",
  "name": "老人姓名关键词",
  "idCardNumber": "身份证号关键词",
  "phone": "电话号码关键词",
  "community": "社区名称",
  "pensionType": "养老类型"
}
```

### 智能设备模块API

```
GET    /api/smart-devices                        - 获取设备列表（支持分页和搜索）
GET    /api/smart-devices/{id}                   - 获取单个设备详情
POST   /api/smart-devices                        - 创建新设备
PUT    /api/smart-devices/{id}                   - 更新设备信息
DELETE /api/smart-devices/{id}                   - 删除设备
DELETE /api/smart-devices/batch                  - 批量删除设备

GET    /api/smart-devices/elderly/{elderlyId}    - 获取指定老人的设备列表
GET    /api/smart-devices/organization/{orgId}   - 获取指定机构的设备列表
GET    /api/smart-devices/statistics/type        - 获取设备类型统计
GET    /api/smart-devices/statistics/status      - 获取设备状态统计
GET    /api/smart-devices/maintenance/needed     - 获取需要维护的设备列表

PUT    /api/smart-devices/{id}/status            - 更新设备状态
PUT    /api/smart-devices/{id}/battery           - 更新设备电量
PUT    /api/smart-devices/{id}/realtime-data     - 更新设备实时数据

GET    /api/device-alarms                        - 获取告警记录列表（支持分页和搜索）
GET    /api/device-alarms/{id}                   - 获取单个告警记录详情
POST   /api/device-alarms                        - 创建告警记录
PUT    /api/device-alarms/{id}                   - 更新告警记录（处理告警）
DELETE /api/device-alarms/{id}                   - 删除告警记录
DELETE /api/device-alarms/batch                  - 批量删除告警记录

GET    /api/device-alarms/device/{deviceId}      - 获取指定设备的告警记录
GET    /api/device-alarms/statistics/type        - 获取告警类型统计
GET    /api/device-alarms/statistics/level       - 获取告警级别统计
GET    /api/device-alarms/unprocessed/count      - 获取未处理告警数量
```

智能设备数据结构：
```json
{
  "id": "设备ID",
  "deviceCode": "设备编号",
  "deviceName": "设备名称",
  "deviceType": "设备类型",
  "deviceBrand": "设备品牌",
  "deviceModel": "设备型号",
  "deviceStatus": "设备状态",
  "purchaseDate": "购买日期",
  "installationLocation": "安装位置",
  "elderlyId": "绑定老人ID",
  "elderlyName": "绑定老人姓名",
  "organizationId": "所属机构ID",
  "organizationName": "机构名称",
  "ipAddress": "IP地址",
  "macAddress": "MAC地址",
  "communicationProtocol": "通信协议",
  "dataCollectionFrequency": "数据采集频率",
  "alarmThreshold": "报警阈值",
  "batteryThreshold": "电量阈值",
  "realTimeData": "实时数据",
  "batteryLevel": "电量百分比",
  "signalStrength": "信号强度",
  "lastCommunicationTime": "最后通信时间",
  "dataUploadStatus": "数据上传状态",
  "warrantyExpiryDate": "保修期限",
  "maintenanceCycle": "维护周期",
  "nextMaintenanceDate": "下次维护时间",
  "failureCount": "故障次数"
}
```

设备告警记录数据结构：
```json
{
  "id": "告警ID",
  "deviceId": "设备ID",
  "deviceName": "设备名称",
  "deviceCode": "设备编号",
  "alarmType": "告警类型",
  "alarmLevel": "告警级别",
  "alarmContent": "告警内容",
  "alarmTime": "告警时间",
  "alarmData": "告警数据",
  "processStatus": "处理状态",
  "processPerson": "处理人员",
  "processTime": "处理时间",
  "processResult": "处理结果",
  "remarks": "备注"
}
```

### 服务记录模块API

```
GET    /api/service-records                      - 获取服务记录列表（支持分页和搜索）
GET    /api/service-records/{id}                 - 获取单个服务记录详情
POST   /api/service-records                      - 创建新服务记录
PUT    /api/service-records/{id}                 - 更新服务记录信息
DELETE /api/service-records/{id}                 - 删除服务记录
DELETE /api/service-records/batch                - 批量删除服务记录

GET    /api/service-records/statistics/status    - 获取服务状态统计
GET    /api/service-records/elderly/{elderlyId}  - 获取指定老人的服务记录
PUT    /api/service-records/{id}/evaluation      - 添加服务评价
```

服务记录数据结构：
```json
{
  "id": "服务记录ID",
  "elderlyId": "老人ID",
  "elderlyName": "老人姓名",
  "serviceContent": "服务内容",
  "serviceTime": "服务时间",
  "serviceAddress": "服务地址",
  "serviceProviderType": "服务提供者类型",
  "serviceProviderId": "服务提供者ID",
  "serviceProviderName": "服务提供者姓名",
  "workOrderPrice": "工单价格",
  "status": "状态",
  "evaluationScore": "评价分数",
  "evaluationComment": "评价内容",
  "createTime": "创建时间",
  "updateTime": "更新时间"
}
```

分页查询参数：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "elderlyName": "老人姓名关键词",
  "serviceContent": "服务内容关键词",
  "serviceProviderName": "服务提供者关键词",
  "status": "状态",
  "startTime": "开始时间",
  "endTime": "结束时间"
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

## 生产环境部署

### Docker部署（推荐）

1. **构建并启动所有服务**：
```bash
docker-compose up --build -d
```

2. **查看服务状态**：
```bash
docker-compose ps
```

3. **查看日志**：
```bash
docker-compose logs -f
```

### 远程服务器部署

项目提供了自动化部署脚本 `deploy.sh`，支持一键部署到远程服务器：

```bash
# 执行部署脚本
./deploy.sh
```

部署流程：
1. 构建后端项目（Maven）
2. 构建前端项目（npm build）
3. 打包项目文件
4. 上传到远程服务器（8.137.85.158）
5. 在远程服务器执行Docker部署

### 服务端口说明

- **前端服务**: 3000端口（开发环境）/ 80端口（生产环境）
- **后端服务**: 8085端口
- **MySQL数据库**: 3306端口
- **Redis缓存**: 6379端口（如果启用）

## 故障排除

### 常见问题

#### 1. 后端服务启动失败
**症状**: 无法访问后端API，ECONNREFUSED错误

**解决方案**:
```bash
# 检查Java版本
java -version

# 检查端口占用
netstat -ano | findstr :8085

# 重新启动后端服务
cd backend
mvn clean install
mvn spring-boot:run
```

#### 2. 数据库连接失败
**症状**: 后端启动时报数据库连接错误

**解决方案**:
```bash
# 检查MySQL服务状态
net start mysql80

# 测试数据库连接
mysql -u root -p -h localhost -P 3306

# 检查数据库配置
# 确认application.properties中的数据库配置正确
```

#### 3. 前端页面空白或加载失败
**症状**: 前端页面无法正常显示

**解决方案**:
```bash
# 清除npm缓存
npm cache clean --force

# 重新安装依赖
rm -rf node_modules package-lock.json
npm install

# 检查代理配置
# 确认vite.config.js中的代理配置指向正确的后端地址
```

#### 4. Docker容器启动失败
**症状**: docker-compose up失败

**解决方案**:
```bash
# 清理Docker资源
docker-compose down
docker system prune -f

# 重新构建镜像
docker-compose build --no-cache
docker-compose up -d

# 查看容器日志
docker-compose logs [service-name]
```

### 性能优化建议

1. **数据库优化**:
   - 为常用查询字段添加索引
   - 定期清理过期数据
   - 配置合适的连接池大小

2. **前端优化**:
   - 启用Gzip压缩
   - 使用CDN加速静态资源
   - 实现懒加载和分页

3. **后端优化**:
   - 配置Redis缓存
   - 使用连接池管理数据库连接
   - 实现API响应缓存

### 监控和日志

- **应用日志**: `backend/logs/`
- **访问日志**: Nginx访问日志
- **错误日志**: 查看Docker容器日志
- **性能监控**: 可集成Prometheus + Grafana

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交改动：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情