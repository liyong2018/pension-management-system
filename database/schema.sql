-- ----------------------------
-- 数据库: pension_management_system
-- ----------------------------
CREATE DATABASE IF NOT EXISTS pension_management_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pension_management_system;

-- ----------------------------
-- 机构表
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '机构ID',
  `name` VARCHAR(255) NOT NULL COMMENT '机构名称',
  `short_name` VARCHAR(100) COMMENT '机构简称',
  `type` VARCHAR(50) COMMENT '机构类型：居家养老单位、社区养老单位（日照）、机构养老单位（养老院）',
  `address` VARCHAR(255) COMMENT '地址',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '电子邮箱',
  `website` VARCHAR(255) COMMENT '网址',
  `establishment_date` DATE COMMENT '成立时间',
  `license_number` VARCHAR(100) COMMENT '许可证号',
  `business_scope` TEXT COMMENT '经营范围',
  `bed_count` INT COMMENT '床位数量',
  `actual_service_count` INT COMMENT '实际服务人数',
  `charging_standard` TEXT COMMENT '收费标准',
  `area` VARCHAR(100) COMMENT '机构规模（占地面积、建筑面积等）',
  `director_name` VARCHAR(50) COMMENT '负责人姓名',
  `director_contact` VARCHAR(100) COMMENT '负责人联系方式',
  `employee_count` INT COMMENT '员工总数',
  `professional_nurse_count` INT COMMENT '专业护理人员数量',
  `fire_license` VARCHAR(255) COMMENT '消防许可证',
  `sanitary_license` VARCHAR(255) COMMENT '卫生许可证',
  `medical_license` VARCHAR(255) COMMENT '医疗机构执业许可证',
  `other_qualifications` TEXT COMMENT '其他资质证书',
  `description` TEXT COMMENT '机构描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机构信息表';

-- ----------------------------
-- 人员档案表 (老人信息)
-- ----------------------------
DROP TABLE IF EXISTS `elderly_profile`;
CREATE TABLE `elderly_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) COMMENT '性别',
  `birth_date` DATE COMMENT '出生日期',
  `id_card_number` VARCHAR(18) UNIQUE COMMENT '身份证号',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `photo_url` VARCHAR(255) COMMENT '照片URL',
  `address_detail` VARCHAR(255) COMMENT '家庭住址',
  `community` VARCHAR(100) COMMENT '所属社区',
  `pension_type` VARCHAR(50) COMMENT '养老类型：居家养老、社区养老（日照）、机构养老（养老院）',
  `medical_history` TEXT COMMENT '过往病史',
  `allergy_history` TEXT COMMENT '过敏史',
  `physical_exam_report` TEXT COMMENT '体检报告（关键指标或文件链接）',
  `current_health_status` TEXT COMMENT '当前健康状况',
  `care_level` VARCHAR(50) COMMENT '护理等级',
  `ability_assessment` VARCHAR(50) COMMENT '能力评估：能力完好、轻度失能、中度失能、重度失能',
  `living_habits` TEXT COMMENT '生活习惯',
  `hobbies` TEXT COMMENT '兴趣爱好',
  `religious_belief` VARCHAR(100) COMMENT '宗教信仰',
  `remarks` TEXT COMMENT '备注',
  `organization_id` BIGINT COMMENT '所属机构ID (如果是机构养老)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员档案表（老人信息）';

-- ----------------------------
-- 老人家属信息表
-- ----------------------------
DROP TABLE IF EXISTS `elderly_family_member`;
CREATE TABLE `elderly_family_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '家属ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '家属姓名',
  `relationship` VARCHAR(50) COMMENT '与老人关系',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人家属信息表';

-- ----------------------------
-- 健康监测数据表
-- ----------------------------
DROP TABLE IF EXISTS `health_monitoring_data`;
CREATE TABLE `health_monitoring_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `device_id` BIGINT COMMENT '设备ID (可选, 如果数据来自特定设备)',
  `monitoring_time` DATETIME NOT NULL COMMENT '监测时间',
  `data_type` VARCHAR(100) COMMENT '数据类型 (如: 心率, 血压, 血糖, 体温, 睡眠)',
  `value` VARCHAR(255) COMMENT '监测值',
  `unit` VARCHAR(50) COMMENT '单位',
  `is_abnormal` BOOLEAN DEFAULT FALSE COMMENT '是否异常',
  `alarm_level` VARCHAR(50) COMMENT '告警级别 (如有)',
  `remarks` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE CASCADE
  -- FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE SET NULL -- 待智能设备表创建后添加
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康监测数据表';

-- ----------------------------
-- 服务记录表
-- ----------------------------
DROP TABLE IF EXISTS `service_record`;
CREATE TABLE `service_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `service_content` TEXT NOT NULL COMMENT '服务内容',
  `service_time` DATETIME NOT NULL COMMENT '服务时间',
  `service_address` VARCHAR(255) COMMENT '服务地址',
  `service_provider_type` VARCHAR(50) COMMENT '服务提供方类型 (如: 机构员工, 志愿者)',
  `service_provider_id` BIGINT COMMENT '服务提供方ID (关联员工表或志愿者表)',
  `service_provider_name` VARCHAR(100) COMMENT '服务提供方姓名',
  `work_order_price` DECIMAL(10, 2) COMMENT '工单价格',
  `status` VARCHAR(50) COMMENT '服务状态 (如: 待处理, 进行中, 已完成, 已评价)',
  `evaluation_score` INT COMMENT '评价分数 (1-5)',
  `evaluation_comment` TEXT COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务记录表';

-- ----------------------------
-- 智能设备表
-- ----------------------------
-- 智能设备表
DROP TABLE IF EXISTS smart_device;
CREATE TABLE smart_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设备ID',
    device_code VARCHAR(100) NOT NULL UNIQUE COMMENT '设备编号',
    device_name VARCHAR(200) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型：健康监测设备/智能家居设备/安全设备/定位设备',
    device_brand VARCHAR(100) COMMENT '设备品牌',
    device_model VARCHAR(100) COMMENT '设备型号',
    device_status VARCHAR(20) NOT NULL DEFAULT '在线' COMMENT '设备状态：在线/离线/故障/维护中',
    purchase_date DATE COMMENT '购买日期',
    installation_location VARCHAR(500) COMMENT '安装位置',
    elderly_id BIGINT COMMENT '绑定的老人ID',
    organization_id BIGINT COMMENT '所属机构ID',
    
    ip_address VARCHAR(50) COMMENT 'IP地址',
    mac_address VARCHAR(50) COMMENT 'MAC地址',
    wifi_config TEXT COMMENT 'Wi-Fi配置信息',
    communication_protocol VARCHAR(50) COMMENT '通信协议：TCP/IP/MQTT/LoRa等',
    data_collection_frequency INT DEFAULT 60 COMMENT '数据采集频率（秒）',
    alarm_threshold TEXT COMMENT '报警阈值配置（JSON格式）',
    battery_threshold INT DEFAULT 20 COMMENT '电量阈值（百分比）',
    
    real_time_data TEXT COMMENT '实时数据（JSON格式）',
    battery_level INT COMMENT '电量百分比',
    signal_strength INT COMMENT '信号强度',
    last_communication_time DATETIME COMMENT '最后通信时间',
    data_upload_status VARCHAR(20) DEFAULT '正常' COMMENT '数据上传状态：正常/异常/延迟',
    
    warranty_expiry_date DATE COMMENT '保修期限',
    maintenance_cycle INT COMMENT '维护周期（天）',
    next_maintenance_date DATE COMMENT '下次维护时间',
    failure_count INT DEFAULT 0 COMMENT '故障次数',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    
    INDEX idx_device_code (device_code),
    INDEX idx_device_type (device_type),
    INDEX idx_device_status (device_status),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (elderly_id) REFERENCES elderly_profile(id) ON DELETE SET NULL,
    FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能设备表';

-- 智能设备告警记录表
DROP TABLE IF EXISTS device_alarm_record;
CREATE TABLE device_alarm_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '告警ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    alarm_type VARCHAR(50) NOT NULL COMMENT '告警类型：设备故障/数据异常/低电量/网络断连/健康异常等',
    alarm_level VARCHAR(20) NOT NULL COMMENT '告警级别：严重/警告/提醒',
    alarm_content TEXT NOT NULL COMMENT '告警内容描述',
    alarm_time DATETIME NOT NULL COMMENT '告警时间',
    alarm_data TEXT COMMENT '触发告警的数据值（JSON格式）',
    
    process_status VARCHAR(20) DEFAULT '未处理' COMMENT '处理状态：未处理/处理中/已处理/已忽略',
    process_person VARCHAR(100) COMMENT '处理人员',
    process_time DATETIME COMMENT '处理时间',
    process_result TEXT COMMENT '处理结果描述',
    remarks TEXT COMMENT '备注',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_device_id (device_id),
    INDEX idx_alarm_type (alarm_type),
    INDEX idx_alarm_level (alarm_level),
    INDEX idx_alarm_time (alarm_time),
    INDEX idx_process_status (process_status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (device_id) REFERENCES smart_device(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能设备告警记录表';

-- 设备维护记录表
DROP TABLE IF EXISTS device_maintenance_record;
CREATE TABLE device_maintenance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '维护记录ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    maintenance_type VARCHAR(50) NOT NULL COMMENT '维护类型：保养/维修/检查/更换',
    maintenance_date DATE NOT NULL COMMENT '维护日期',
    maintenance_person VARCHAR(100) COMMENT '维护人员',
    maintenance_content TEXT COMMENT '维护内容',
    maintenance_result VARCHAR(20) COMMENT '维护结果：正常/异常/需更换',
    cost DECIMAL(10,2) COMMENT '维护费用',
    next_maintenance_date DATE COMMENT '下次维护日期',
    remarks TEXT COMMENT '备注',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(100) COMMENT '创建人',
    
    INDEX idx_device_id (device_id),
    INDEX idx_maintenance_type (maintenance_type),
    INDEX idx_maintenance_date (maintenance_date),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (device_id) REFERENCES smart_device(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备维护记录表';

-- 设备数据采集记录表
DROP TABLE IF EXISTS device_data_record;
CREATE TABLE device_data_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '数据记录ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    elderly_id BIGINT COMMENT '老人ID',
    data_type VARCHAR(50) NOT NULL COMMENT '数据类型：健康数据/环境数据/安全数据/行为数据',
    data_content TEXT NOT NULL COMMENT '数据内容（JSON格式）',
    data_time DATETIME NOT NULL COMMENT '数据采集时间',
    data_value DECIMAL(10,2) COMMENT '数据数值（用于统计分析）',
    data_unit VARCHAR(20) COMMENT '数据单位',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常：0-正常，1-异常',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_device_id (device_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_data_type (data_type),
    INDEX idx_data_time (data_time),
    INDEX idx_created_time (created_time),
    INDEX idx_is_abnormal (is_abnormal),
    FOREIGN KEY (device_id) REFERENCES smart_device(id) ON DELETE CASCADE,
    FOREIGN KEY (elderly_id) REFERENCES elderly_profile(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备数据采集记录表'; 
-- ----------------------------
-- 志愿者信息表
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '志愿者ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) COMMENT '性别',
  `age` INT COMMENT '年龄',
  `id_card_number` VARCHAR(18) UNIQUE COMMENT '身份证号码',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系方式',
  `email` VARCHAR(100) COMMENT '电子邮箱',
  `address_detail` VARCHAR(255) COMMENT '常住地址',
  `household_address` VARCHAR(255) COMMENT '户籍地址',
  `education` VARCHAR(50) COMMENT '最高学历',
  `graduation_school` VARCHAR(100) COMMENT '毕业院校',
  `occupation` VARCHAR(100) COMMENT '职业',
  `work_unit` VARCHAR(255) COMMENT '工作单位',
  `professional_skills` TEXT COMMENT '专业技能',
  `hobbies` TEXT COMMENT '兴趣爱好',
  `language_ability` VARCHAR(255) COMMENT '语言能力',
  `service_intention` TEXT COMMENT '服务意向',
  `available_time` TEXT COMMENT '服务时间说明 (如: 每周一、三、五上午)',
  `service_experience` TEXT COMMENT '服务经历',
  `emergency_contact_name` VARCHAR(50) COMMENT '紧急联系人姓名',
  `emergency_contact_relationship` VARCHAR(50) COMMENT '与志愿者关系',
  `emergency_contact_phone` VARCHAR(20) COMMENT '紧急联系人电话',
  `registration_date` DATE COMMENT '注册日期',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE, INACTIVE, PENDING_APPROVAL)',
  `total_service_hours` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '累计服务时长',
  `points` INT DEFAULT 0 COMMENT '积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者信息表';

-- ----------------------------
-- 志愿者服务项目表 (平台发布的服务需求)
-- ----------------------------
DROP TABLE IF EXISTS `volunteer_service_project`;
CREATE TABLE `volunteer_service_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
  `description` TEXT COMMENT '项目描述',
  `service_category` VARCHAR(100) COMMENT '服务类别 (如: 生活照料, 陪伴聊天, 康复护理, 文化娱乐)',
  `start_time` DATETIME COMMENT '项目开始时间',
  `end_time` DATETIME COMMENT '项目结束时间',
  `location` VARCHAR(255) COMMENT '服务地点',
  `recruitment_count` INT COMMENT '招募人数',
  `current_enrolled_count` INT DEFAULT 0 COMMENT '当前报名人数',
  `requirements` TEXT COMMENT '志愿者要求',
  `status` VARCHAR(50) DEFAULT 'OPEN' COMMENT '项目状态 (OPEN, CLOSED, IN_PROGRESS, COMPLETED)',
  `organization_id` BIGINT COMMENT '发布机构ID (如果是机构发起的项目)',
  `created_by_user_id` BIGINT COMMENT '创建人ID (关联用户表)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
  -- FOREIGN KEY (`created_by_user_id`) REFERENCES `user`(`id`) ON DELETE SET NULL -- 待用户表创建后添加
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者服务项目表';

-- ----------------------------
-- 志愿者服务记录表 (志愿者参与服务的记录)
-- ----------------------------
DROP TABLE IF EXISTS `volunteer_service_assignment`;
CREATE TABLE `volunteer_service_assignment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分配/记录ID',
  `volunteer_id` BIGINT NOT NULL COMMENT '志愿者ID',
  `project_id` BIGINT COMMENT '关联的服务项目ID (如果是参与项目)',
  `elderly_id` BIGINT COMMENT '服务对象老人ID (如果是直接服务老人)',
  `service_date` DATE NOT NULL COMMENT '服务日期',
  `service_start_time` TIME COMMENT '服务开始时间',
  `service_end_time` TIME COMMENT '服务结束时间',
  `service_duration_hours` DECIMAL(5, 2) COMMENT '服务时长（小时）',
  `service_content` TEXT COMMENT '服务内容描述',
  `feedback_from_elderly` TEXT COMMENT '老人或家属反馈',
  `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态 (PENDING, CONFIRMED, COMPLETED, CANCELED)',
  `points_awarded` INT DEFAULT 0 COMMENT '授予积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`volunteer_id`) REFERENCES `volunteer`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`project_id`) REFERENCES `volunteer_service_project`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者服务记录与分配表';

-- ----------------------------
-- 系统用户表 (用于后台管理)
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `full_name` VARCHAR(100) COMMENT '姓名',
  `email` VARCHAR(100) UNIQUE COMMENT '电子邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `organization_id` BIGINT COMMENT '所属机构ID (如果是机构用户)',
  `is_admin` BOOLEAN DEFAULT FALSE COMMENT '是否为超级管理员',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称 (如: 系统管理员, 机构管理员, 护理员, 志愿者管理员)',
  `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色键 (用于代码中判断)',
  `description` TEXT COMMENT '角色描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `system_user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 菜单/权限表
-- ----------------------------
DROP TABLE IF EXISTS `menu_permission`;
CREATE TABLE `menu_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父权限ID (用于树形结构)',
  `name` VARCHAR(100) NOT NULL COMMENT '菜单/权限名称',
  `type` VARCHAR(20) NOT NULL COMMENT '类型 (MENU:菜单项, BUTTON:按钮权限, API:接口权限)',
  `permission_key` VARCHAR(100) UNIQUE COMMENT '权限标识符 (如: elderly:list, elderly:create)',
  `route_path` VARCHAR(255) COMMENT '前端路由路径 (如果是菜单)',
  `component_path` VARCHAR(255) COMMENT '前端组件路径 (如果是菜单)',
  `icon` VARCHAR(100) COMMENT '菜单图标',
  `sort_order` INT DEFAULT 0 COMMENT '显示顺序',
  `is_visible` BOOLEAN DEFAULT TRUE COMMENT '是否在菜单中显示',
  `status` BOOLEAN DEFAULT TRUE COMMENT '状态 (启用/禁用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`parent_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ----------------------------
-- 角色菜单/权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 首页统计数据表 (考虑是否需要持久化，或者实时计算)
-- 可以创建一个表来存储每日或定期的快照，或者在需要时通过聚合查询生成。
-- 此处仅为示例，实际可能通过视图或定时任务更新。
-- ----------------------------
DROP TABLE IF EXISTS `homepage_statistics`;
CREATE TABLE `homepage_statistics` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `record_date` DATE NOT NULL UNIQUE COMMENT '记录日期',
  `elderly_population_count` INT COMMENT '老龄人口总数',
  `organization_facility_count` INT COMMENT '养老机构及设施总数',
  `practitioner_count` INT COMMENT '从业人员总数',
  `subsidy_amount_total` DECIMAL(15,2) COMMENT '发放补贴总额',
  `elderly_type_normal_count` INT COMMENT '老人类型-正常老人数量',
  `elderly_type_empty_nester_count` INT COMMENT '老人类型-空巢老人数量',
  `elderly_type_living_alone_count` INT COMMENT '老人类型-独居老人数量',
  `elderly_type_disabled_count` INT COMMENT '老人类型-残疾老人数量',
  `elderly_type_advanced_age_count` INT COMMENT '老人类型-高龄老人数量',
  `ability_assessment_intact_count` INT COMMENT '能力评估-能力完好数量',
  `ability_assessment_mild_disability_count` INT COMMENT '能力评估-轻度失能数量',
  `ability_assessment_moderate_disability_count` INT COMMENT '能力评估-中度失能数量',
  `ability_assessment_severe_disability_count` INT COMMENT '能力评估-重度失能数量',
  `ability_assessment_unassessed_count` INT COMMENT '能力评估-未评估数量',
  -- 年龄分布可以更细化，例如存储JSON或多个字段
  `age_distribution_60_69_count` INT COMMENT '年龄分布-60至69岁数量',
  `age_distribution_70_79_count` INT COMMENT '年龄分布-70至79岁数量',
  `age_distribution_80_89_count` INT COMMENT '年龄分布-80至89岁数量',
  `age_distribution_90_plus_count` INT COMMENT '年龄分布-90岁以上数量',
  `connected_devices_count` INT COMMENT '接入设备总数',
  `device_status_online_count` INT COMMENT '设备状态-在线数量',
  `device_status_offline_count` INT COMMENT '设备状态-离线数量',
  `device_status_fault_count` INT COMMENT '设备状态-故障数量',
  `total_alarms_today` INT COMMENT '今日告警总数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页统计数据快照表';

-- ----------------------------
-- 初始化一些基础数据 (可选)
-- ----------------------------
INSERT INTO `role` (`role_name`, `role_key`, `description`) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('机构管理员', 'ORG_ADMIN', '管理本机构相关事务'),
('普通用户', 'USER', '基本查看权限');

-- 假设超级管理员用户
INSERT INTO `system_user` (`username`, `password_hash`, `full_name`, `is_admin`, `is_active`) VALUES
('admin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '超级管理员', TRUE, TRUE); -- 密码 'admin' 的 bcrypt 哈希 (请替换为安全的哈希值)

-- 给admin用户分配超级管理员角色 (假设admin用户ID为1, 超级管理员角色ID为1)
-- INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 修正外键约束，在所有表创建完成后
ALTER TABLE `health_monitoring_data`
  ADD CONSTRAINT `fk_health_data_device` FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE SET NULL;

ALTER TABLE `device_alarm_record`
  ADD CONSTRAINT `fk_alarm_processor` FOREIGN KEY (`processor_id`) REFERENCES `system_user`(`id`) ON DELETE SET NULL;

ALTER TABLE `volunteer_service_project`
  ADD CONSTRAINT `fk_project_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `system_user`(`id`) ON DELETE SET NULL;


-- 注意：
-- 1. `password_hash` 需要使用安全的哈希算法（如 bcrypt）存储，示例中的哈希值是占位符。
-- 2. 一些统计数据，如首页的统计，可以考虑通过视图（View）或定时任务从其他表中聚合生成，而不是直接存储，以保证数据实时性。
--    如果需要历史趋势，则可以像 `homepage_statistics` 表一样做快照。
-- 3. `*_id` 字段，如果需要关联到其他模块但该模块表尚未创建，可以先注释掉外键，待所有表结构确定后再添加。
-- 4. 地址信息可以考虑更规范的省市区街道表，并通过外键关联，此处为简化直接存储字符串。
-- 5. 某些字段如 '养老类型', '能力评估' 等，可以使用枚举类型或关联字典表，此处为简化使用 VARCHAR。


-- 机构类型示例 (可以作为字典表)
-- CREATE TABLE `dict_organization_type` (
--   `id` INT AUTO_INCREMENT PRIMARY KEY,
--   `name` VARCHAR(50) NOT NULL UNIQUE,
--   `description` VARCHAR(255)
-- );
-- INSERT INTO `dict_organization_type` (name, description) VALUES
-- ('HOME_CARE', '居家养老单位'),
-- ('COMMUNITY_CARE_DAY', '社区养老单位（日照）'),
-- ('INSTITUTIONAL_CARE', '机构养老单位（养老院）');

-- 养老类型示例 (可以作为字典表)
-- CREATE TABLE `dict_pension_type` (
--   `id` INT AUTO_INCREMENT PRIMARY KEY,
--   `name` VARCHAR(50) NOT NULL UNIQUE,
--   `description` VARCHAR(255)
-- );
-- INSERT INTO `dict_pension_type` (name, description) VALUES
-- ('HOME', '居家养老'),
-- ('COMMUNITY_DAY', '社区养老（日照）'),
-- ('INSTITUTIONAL', '机构养老（养老院）');

-- 能力评估示例 (可以作为字典表)
-- CREATE TABLE `dict_ability_assessment` (
--   `id` INT AUTO_INCREMENT PRIMARY KEY,
--   `name` VARCHAR(50) NOT NULL UNIQUE,
--   `description` VARCHAR(255)
-- );
-- INSERT INTO `dict_ability_assessment` (name, description) VALUES
-- ('INTACT', '能力完好'),
-- ('MILD_DISABILITY', '轻度失能'),
-- ('MODERATE_DISABILITY', '中度失能'),
-- ('SEVERE_DISABILITY', '重度失能'),
-- ('UNASSESSED', '未评估');

-- 设备类型示例 (可以作为字典表)
-- CREATE TABLE `dict_device_type` (
--   `id` INT AUTO_INCREMENT PRIMARY KEY,
--   `name` VARCHAR(100) NOT NULL UNIQUE,
--   `description` VARCHAR(255)
-- );
-- INSERT INTO `dict_device_type` (name, description) VALUES
-- ('SOS_ALARM', 'SOS报警器'),
-- ('SMOKE_SENSOR', '烟雾传感器'),
-- ('WATER_LEAK_SENSOR', '水浸传感器'),
-- ('FALL_DETECTOR', '跌倒检测器'),
-- ('GAS_LEAK_SENSOR', '燃气泄露传感器'),
-- ('HEALTH_BRACELET', '健康监测手环'),
-- ('BLOOD_PRESSURE_MONITOR', '血压计');





</rewritten_file> 