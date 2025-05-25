-- ----------------------------
-- 数据库: pension_management_system (修复版本)
-- ----------------------------

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 首先删除所有表（按照外键依赖的相反顺序）
DROP TABLE IF EXISTS `dictionary`;
DROP TABLE IF EXISTS `operation_log`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `menu_permission`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `homepage_statistics`;
DROP TABLE IF EXISTS `volunteer_service_assignment`;
DROP TABLE IF EXISTS `volunteer_service_project`;
DROP TABLE IF EXISTS `device_data_record`;
DROP TABLE IF EXISTS `device_maintenance_record`;
DROP TABLE IF EXISTS `device_alarm_record`;
DROP TABLE IF EXISTS `health_monitoring_data`;
DROP TABLE IF EXISTS `smart_device`;
DROP TABLE IF EXISTS `service_record`;
DROP TABLE IF EXISTS `elderly_family_member`;
DROP TABLE IF EXISTS `elderly_profile`;
DROP TABLE IF EXISTS `volunteer`;
DROP TABLE IF EXISTS `system_user`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `organization`;

-- 创建表（按照外键依赖的正确顺序）

-- 1. 基础表（无外键依赖）
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

-- 2. 角色表（无外键依赖）
CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色键',
  `description` TEXT COMMENT '角色描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 志愿者表（无外键依赖）
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
  `available_time` TEXT COMMENT '服务时间说明',
  `service_experience` TEXT COMMENT '服务经历',
  `emergency_contact_name` VARCHAR(50) COMMENT '紧急联系人姓名',
  `emergency_contact_relationship` VARCHAR(50) COMMENT '与志愿者关系',
  `emergency_contact_phone` VARCHAR(20) COMMENT '紧急联系人电话',
  `registration_date` DATE COMMENT '注册日期',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `total_service_hours` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '累计服务时长',
  `points` INT DEFAULT 0 COMMENT '积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者信息表';

-- 4. 系统用户表（依赖organization）
CREATE TABLE `system_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `full_name` VARCHAR(100) COMMENT '姓名',
  `email` VARCHAR(100) UNIQUE COMMENT '电子邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `organization_id` BIGINT COMMENT '所属机构ID',
  `is_admin` BOOLEAN DEFAULT FALSE COMMENT '是否为超级管理员',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 5. 老人档案表（依赖organization）
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
  `pension_type` VARCHAR(50) COMMENT '养老类型',
  `medical_history` TEXT COMMENT '过往病史',
  `allergy_history` TEXT COMMENT '过敏史',
  `physical_exam_report` TEXT COMMENT '体检报告',
  `current_health_status` TEXT COMMENT '当前健康状况',
  `care_level` VARCHAR(50) COMMENT '护理等级',
  `ability_assessment` VARCHAR(50) COMMENT '能力评估',
  `living_habits` TEXT COMMENT '生活习惯',
  `hobbies` TEXT COMMENT '兴趣爱好',
  `religious_belief` VARCHAR(100) COMMENT '宗教信仰',
  `remarks` TEXT COMMENT '备注',
  `organization_id` BIGINT COMMENT '所属机构ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员档案表';

-- 6. 用户角色关联表（依赖system_user和role）
CREATE TABLE `user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `system_user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 7. 菜单权限表（自引用外键）
CREATE TABLE `menu_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父权限ID',
  `name` VARCHAR(100) NOT NULL COMMENT '菜单/权限名称',
  `type` VARCHAR(20) NOT NULL COMMENT '类型',
  `permission_key` VARCHAR(100) UNIQUE COMMENT '权限标识符',
  `route_path` VARCHAR(255) COMMENT '前端路由路径',
  `component_path` VARCHAR(255) COMMENT '前端组件路径',
  `icon` VARCHAR(100) COMMENT '菜单图标',
  `sort_order` INT DEFAULT 0 COMMENT '显示顺序',
  `is_visible` BOOLEAN DEFAULT TRUE COMMENT '是否在菜单中显示',
  `status` BOOLEAN DEFAULT TRUE COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`parent_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 8. 角色权限关联表（依赖role和menu_permission）
CREATE TABLE `role_permission` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 9. 老人家属信息表（依赖elderly_profile）
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

-- 10. 智能设备表（依赖elderly_profile和organization）
CREATE TABLE `smart_device` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设备ID',
    `device_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '设备编号',
    `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
    `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型',
    `device_brand` VARCHAR(100) COMMENT '设备品牌',
    `device_model` VARCHAR(100) COMMENT '设备型号',
    `device_status` VARCHAR(20) NOT NULL DEFAULT '在线' COMMENT '设备状态',
    `purchase_date` DATE COMMENT '购买日期',
    `installation_location` VARCHAR(500) COMMENT '安装位置',
    `elderly_id` BIGINT COMMENT '绑定的老人ID',
    `organization_id` BIGINT COMMENT '所属机构ID',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `mac_address` VARCHAR(50) COMMENT 'MAC地址',
    `wifi_config` TEXT COMMENT 'Wi-Fi配置信息',
    `communication_protocol` VARCHAR(50) COMMENT '通信协议',
    `data_collection_frequency` INT DEFAULT 60 COMMENT '数据采集频率（秒）',
    `alarm_threshold` TEXT COMMENT '报警阈值配置',
    `battery_threshold` INT DEFAULT 20 COMMENT '电量阈值',
    `real_time_data` TEXT COMMENT '实时数据',
    `battery_level` INT COMMENT '电量百分比',
    `signal_strength` INT COMMENT '信号强度',
    `last_communication_time` DATETIME COMMENT '最后通信时间',
    `data_upload_status` VARCHAR(20) DEFAULT '正常' COMMENT '数据上传状态',
    `warranty_expiry_date` DATE COMMENT '保修期限',
    `maintenance_cycle` INT COMMENT '维护周期（天）',
    `next_maintenance_date` DATE COMMENT '下次维护时间',
    `failure_count` INT DEFAULT 0 COMMENT '故障次数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) COMMENT '创建人',
    `updated_by` VARCHAR(100) COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_device_code (`device_code`),
    INDEX idx_device_type (`device_type`),
    INDEX idx_device_status (`device_status`),
    INDEX idx_elderly_id (`elderly_id`),
    INDEX idx_organization_id (`organization_id`),
    INDEX idx_created_time (`created_time`),
    FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能设备表';

-- 11. 服务记录表（依赖elderly_profile）
CREATE TABLE `service_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `service_content` TEXT NOT NULL COMMENT '服务内容',
  `service_time` DATETIME NOT NULL COMMENT '服务时间',
  `service_address` VARCHAR(255) COMMENT '服务地址',
  `service_type` VARCHAR(100) COMMENT '服务类型',
  `service_duration` DECIMAL(5, 2) COMMENT '服务时长（小时）',
  `service_provider_type` VARCHAR(50) COMMENT '服务提供方类型',
  `service_provider_id` BIGINT COMMENT '服务提供方ID',
  `service_provider_name` VARCHAR(100) COMMENT '服务提供方姓名',
  `work_order_price` DECIMAL(10, 2) COMMENT '工单价格',
  `status` VARCHAR(50) COMMENT '服务状态',
  `evaluation_score` INT COMMENT '评价分数',
  `evaluation_comment` TEXT COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务记录表';

-- 12. 健康监测数据表（依赖elderly_profile和smart_device）
CREATE TABLE `health_monitoring_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `device_id` BIGINT COMMENT '设备ID',
  `monitoring_time` DATETIME NOT NULL COMMENT '监测时间',
  `data_type` VARCHAR(100) COMMENT '数据类型',
  `value` VARCHAR(255) COMMENT '监测值',
  `unit` VARCHAR(50) COMMENT '单位',
  `is_abnormal` BOOLEAN DEFAULT FALSE COMMENT '是否异常',
  `alarm_level` VARCHAR(50) COMMENT '告警级别',
  `remarks` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康监测数据表';

-- 13. 设备告警记录表（依赖smart_device）
CREATE TABLE `device_alarm_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '告警ID',
    `device_id` BIGINT NOT NULL COMMENT '设备ID',
    `alarm_type` VARCHAR(50) NOT NULL COMMENT '告警类型',
    `alarm_level` VARCHAR(20) NOT NULL COMMENT '告警级别',
    `alarm_content` TEXT NOT NULL COMMENT '告警内容描述',
    `alarm_time` DATETIME NOT NULL COMMENT '告警时间',
    `alarm_data` TEXT COMMENT '触发告警的数据值',
    `process_status` VARCHAR(20) DEFAULT '未处理' COMMENT '处理状态',
    `process_person` VARCHAR(100) COMMENT '处理人员',
    `process_time` DATETIME COMMENT '处理时间',
    `process_result` TEXT COMMENT '处理结果描述',
    `remarks` TEXT COMMENT '备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_device_id (`device_id`),
    INDEX idx_alarm_type (`alarm_type`),
    INDEX idx_alarm_level (`alarm_level`),
    INDEX idx_alarm_time (`alarm_time`),
    INDEX idx_process_status (`process_status`),
    INDEX idx_created_time (`created_time`),
    FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能设备告警记录表';

-- 14. 志愿者服务项目表（依赖organization和system_user）
CREATE TABLE `volunteer_service_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
  `description` TEXT COMMENT '项目描述',
  `service_category` VARCHAR(100) COMMENT '服务类别',
  `start_time` DATETIME COMMENT '项目开始时间',
  `end_time` DATETIME COMMENT '项目结束时间',
  `location` VARCHAR(255) COMMENT '服务地点',
  `recruitment_count` INT COMMENT '招募人数',
  `current_enrolled_count` INT DEFAULT 0 COMMENT '当前报名人数',
  `requirements` TEXT COMMENT '志愿者要求',
  `status` VARCHAR(50) DEFAULT 'OPEN' COMMENT '项目状态',
  `organization_id` BIGINT COMMENT '发布机构ID',
  `created_by_user_id` BIGINT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`created_by_user_id`) REFERENCES `system_user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者服务项目表';

-- 15. 志愿者服务记录表（依赖volunteer、volunteer_service_project、elderly_profile）
CREATE TABLE `volunteer_service_assignment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分配/记录ID',
  `volunteer_id` BIGINT NOT NULL COMMENT '志愿者ID',
  `project_id` BIGINT COMMENT '关联的服务项目ID',
  `elderly_id` BIGINT COMMENT '服务对象老人ID',
  `service_date` DATE NOT NULL COMMENT '服务日期',
  `service_start_time` TIME COMMENT '服务开始时间',
  `service_end_time` TIME COMMENT '服务结束时间',
  `service_duration_hours` DECIMAL(5, 2) COMMENT '服务时长（小时）',
  `service_content` TEXT COMMENT '服务内容描述',
  `feedback_from_elderly` TEXT COMMENT '老人或家属反馈',
  `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态',
  `points_awarded` INT DEFAULT 0 COMMENT '授予积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`volunteer_id`) REFERENCES `volunteer`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`project_id`) REFERENCES `volunteer_service_project`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者服务记录与分配表';

-- 16. 设备维护记录表（依赖smart_device）
CREATE TABLE `device_maintenance_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '维护记录ID',
    `device_id` BIGINT NOT NULL COMMENT '设备ID',
    `maintenance_type` VARCHAR(50) NOT NULL COMMENT '维护类型',
    `maintenance_date` DATE NOT NULL COMMENT '维护日期',
    `maintenance_person` VARCHAR(100) COMMENT '维护人员',
    `maintenance_content` TEXT COMMENT '维护内容',
    `maintenance_result` VARCHAR(20) COMMENT '维护结果',
    `cost` DECIMAL(10,2) COMMENT '维护费用',
    `next_maintenance_date` DATE COMMENT '下次维护日期',
    `remarks` TEXT COMMENT '备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) COMMENT '创建人',
    INDEX idx_device_id (`device_id`),
    INDEX idx_maintenance_type (`maintenance_type`),
    INDEX idx_maintenance_date (`maintenance_date`),
    INDEX idx_created_time (`created_time`),
    FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备维护记录表';

-- 17. 设备数据采集记录表（依赖smart_device和elderly_profile）
CREATE TABLE `device_data_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '数据记录ID',
    `device_id` BIGINT NOT NULL COMMENT '设备ID',
    `elderly_id` BIGINT COMMENT '老人ID',
    `data_type` VARCHAR(50) NOT NULL COMMENT '数据类型',
    `data_content` TEXT NOT NULL COMMENT '数据内容',
    `data_time` DATETIME NOT NULL COMMENT '数据采集时间',
    `data_value` DECIMAL(10,2) COMMENT '数据数值',
    `data_unit` VARCHAR(20) COMMENT '数据单位',
    `is_abnormal` TINYINT DEFAULT 0 COMMENT '是否异常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_device_id (`device_id`),
    INDEX idx_elderly_id (`elderly_id`),
    INDEX idx_data_type (`data_type`),
    INDEX idx_data_time (`data_time`),
    INDEX idx_created_time (`created_time`),
    INDEX idx_is_abnormal (`is_abnormal`),
    FOREIGN KEY (`device_id`) REFERENCES `smart_device`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备数据采集记录表';

-- 18. 首页统计数据表（无外键依赖）
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

-- 19. 操作日志表（依赖system_user）
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `username` VARCHAR(100) NOT NULL COMMENT '操作用户',
  `user_id` BIGINT COMMENT '用户ID',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_desc` TEXT COMMENT '操作描述',
  `module` VARCHAR(100) COMMENT '操作模块',
  `log_level` VARCHAR(20) DEFAULT 'INFO' COMMENT '日志级别',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` TEXT COMMENT '用户代理',
  `request_url` VARCHAR(500) COMMENT '请求URL',
  `request_method` VARCHAR(10) COMMENT '请求方法',
  `request_params` TEXT COMMENT '请求参数',
  `response_data` TEXT COMMENT '响应结果',
  `request_time` INT COMMENT '请求时长（毫秒）',
  `error_message` TEXT COMMENT '错误信息',
  `error_stack` TEXT COMMENT '错误堆栈',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_username` (`username`),
  INDEX `idx_operation_type` (`operation_type`),
  INDEX `idx_log_level` (`log_level`),
  INDEX `idx_module` (`module`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `system_user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
-- 19. 字典表
CREATE TABLE IF NOT EXISTS `dictionary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
  `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(200) NOT NULL COMMENT '字典值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(启用), INACTIVE(禁用)',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type`, `dict_code`),
  INDEX `idx_dict_type` (`dict_type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';
-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================
-- 插入初始数据（按正确的依赖顺序）
-- ============================

-- 1. 插入角色数据
INSERT INTO `role` (`role_name`, `role_key`, `description`) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('机构管理员', 'ORG_ADMIN', '管理本机构相关事务'),
('普通用户', 'USER', '基本查看权限');

-- 2. 插入机构数据
INSERT INTO `organization` (`name`, `short_name`, `type`, `address`, `phone`, `email`, `website`, `establishment_date`, `license_number`, `business_scope`, `bed_count`, `actual_service_count`, `charging_standard`, `area`, `director_name`, `director_contact`, `employee_count`, `professional_nurse_count`, `fire_license`, `sanitary_license`, `medical_license`, `other_qualifications`) VALUES 
('阳光老年公寓', '阳光公寓', '机构养老单位（养老院）', '北京市朝阳区阳光路123号', '010-12345678', 'yangguang@example.com', 'www.yangguang.com', '2015-01-15',
'YL2015001', '提供养老服务、医疗保健、文化娱乐等综合服务', 200, 180, '标准间：3000元/月；单人间：4000元/月；特需间：6000元/月',
'占地面积：5000㎡，建筑面积：8000㎡', '张明', '13901234567', 50, 20,
'XF202301001', 'WS202301001', 'YY202301001', '养老机构等级评定5A级'),

('和谐养老院', '和谐院', '机构养老单位（养老院）', '上海市浦东新区和谐路456号', '021-87654321', 'hexie@example.com', 'www.hexie.com', '2016-03-20',
'YL2016002', '养老照护、康复理疗、营养配餐', 150, 130, '双人间：3500元/月；单人间：4500元/月',
'占地面积：4000㎡，建筑面积：6000㎡', '李华', '13812345678', 40, 15,
'XF202302002', 'WS202302002', 'YY202302002', '医养结合示范单位'),

('乐龄社区日照中心', '乐龄中心', '社区养老单位（日照）', '广州市天河区乐龄路789号', '020-98765432', 'leling@example.com', 'www.leling.com', '2017-05-10',
'YL2017003', '日间照料、康复训练、文娱活动', 0, 50, '日托：100元/天；半月托：1400元/月；月托：2600元/月',
'建筑面积：1200㎡', '王芳', '13923456789', 15, 5,
'XF202303003', 'WS202303003', 'YY202303003', '社区养老示范点');

-- 3. 插入系统用户数据
INSERT INTO `system_user` (`username`, `password_hash`, `full_name`, `email`, `is_admin`, `is_active`, `create_time`)
VALUES ('admin', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '系统管理员', 'admin@example.com', true, true, CURRENT_TIMESTAMP);

-- 4. 插入用户角色关联数据
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 5. 插入老人档案数据
INSERT INTO `elderly_profile` (`name`, `gender`, `birth_date`, `id_card_number`, `phone`, `photo_url`, `address_detail`, `community`, `pension_type`, `medical_history`, `allergy_history`, `physical_exam_report`, `current_health_status`, `care_level`, `ability_assessment`, `living_habits`, `hobbies`, `religious_belief`, `remarks`, `organization_id`) VALUES 
('王建国', '男', '1938-03-15', '110101193803152011', '13601234567', 'https://i.pravatar.cc/150?img=1',
 '北京市朝阳区朝阳公园南路123号', '朝阳公园社区', '居家养老', 
 '高血压病史15年，糖尿病史8年，曾患脑梗塞', '青霉素过敏', 
 '2024-11-01体检：血压150/90mmHg，血糖7.2mmol/L，心电图正常', 
 '血压血糖控制良好，行动能力正常', '二级护理', '能力完好',
 '早睡早起，喜欢晨练，饮食清淡', '太极拳、象棋、书法', '无', 
 '退休干部，生活自理能力强', 1),

('李秀梅', '女', '1941-07-22', '110101194107228524', '13701234568', 'https://i.pravatar.cc/150?img=2',
 '北京市海淀区中关村大街45号', '中关村社区', '社区养老（日照）',
 '骨质疏松，轻度认知障碍，慢性胃炎', '无明显过敏史',
 '2024-10-15体检：骨密度偏低，记忆力测试轻度异常', 
 '需要适当看护，容易忘事', '三级护理', '轻度失能',
 '喜欢听戏，午休时间长', '京剧、编织、养花', '佛教',
 '需要提醒服药，家属每日探视', 2),

('张志强', '男', '1935-12-08', '110101193512083719', '13801234569', 'https://i.pravatar.cc/150?img=3',
 '北京市西城区西单北大街78号', '西单社区', '机构养老（养老院）',
 '冠心病，轻度脑萎缩，前列腺增生', '磺胺类药物过敏',
 '2024-09-20体检：心脏彩超示轻度冠脉狭窄，认知功能尚可',
 '心脏功能稍差，需要定期复查', '三级护理', '轻度失能',
 '作息规律，饮食需要低盐低脂', '读报、听广播、下棋', '无',
 '入住养老院3年，适应良好', 3);

-- 6. 插入家属信息数据
INSERT INTO `elderly_family_member` (`elderly_id`, `name`, `relationship`, `phone`) VALUES 
(1, '王小明', '儿子', '13811111111'),
(1, '张丽华', '儿媳', '13812222222'),
(2, '李强', '儿子', '13821111111'),
(2, '王美玲', '女儿', '13822222222'),
(3, '张伟', '儿子', '13831111111'),
(3, '张敏', '女儿', '13832222222');

-- 7. 插入服务记录数据
INSERT INTO `service_record` (`elderly_id`, `service_content`, `service_time`, `service_address`, `service_type`, `service_duration`, `service_provider_type`, `service_provider_name`, `work_order_price`, `status`) VALUES
(1, '日常生活护理服务', '2024-01-15 10:00:00', '北京市朝阳区', '生活照料', 2.00, '机构员工', '张护士', 50.00, '已完成'),
(2, '健康检查服务', '2024-01-16 14:30:00', '北京市海淀区', '医疗护理', 0.50, '机构员工', '李医生', 80.00, '进行中'),
(3, '医疗咨询服务', '2024-01-15 10:00:00', '北京朝阳养老院', '医疗护理', 0.50, '志愿者', '王志愿者', 0.00, '待处理');

-- 8. 插入智能设备数据
INSERT INTO `smart_device` (`device_code`, `device_name`, `device_type`, `device_brand`, `device_model`, `device_status`, `purchase_date`, `installation_location`, `elderly_id`, `organization_id`, `ip_address`, `mac_address`, `communication_protocol`, `data_collection_frequency`, `alarm_threshold`, `battery_threshold`, `real_time_data`, `battery_level`, `signal_strength`, `last_communication_time`, `data_upload_status`, `warranty_expiry_date`, `maintenance_cycle`, `next_maintenance_date`, `failure_count`, `created_time`, `updated_time`, `created_by`, `is_deleted`) VALUES

('DEV001', '智能手环Pro', '健康监测设备', '小米', 'Mi Band 7', '在线', 
 '2023-01-15', '张伟 卧室', 1, 1,
 '192.168.1.101', 'AA:BB:CC:DD:EE:01', 'MQTT', 60,
 '{"heart_rate_max": 120, "heart_rate_min": 60}', 20, 
 '{"heart_rate": 78, "steps": 8500, "battery": 85}', 85, 85,
 '2024-01-15 14:30:00', '正常', '2025-01-15', 30, '2024-02-15', 0,
 '2023-01-15 10:00:00', '2024-01-15 14:30:00', 'admin', 0),

('DEV002', '血压监测仪', '健康监测设备', '欧姆龙', 'HEM-7156', '在线',
 '2023-02-20', '李淑华 客厅', 2, 1,
 '192.168.1.102', 'AA:BB:CC:DD:EE:02', 'TCP/IP', 300,
 '{"systolic_max": 140, "diastolic_max": 90}', 15,
 '{"systolic": 125, "diastolic": 80, "battery": 70}', 70, 90,
 '2024-01-15 14:00:00', '正常', '2025-02-20', 60, '2024-03-20', 0,
 '2023-02-20 09:00:00', '2024-01-15 14:00:00', 'admin', 0);

-- 9. 插入设备告警记录数据
INSERT INTO `device_alarm_record` (`device_id`, `alarm_type`, `alarm_level`, `alarm_content`, `alarm_time`, `alarm_data`, `process_status`, `process_person`, `process_time`, `process_result`, `remarks`, `created_time`, `updated_time`) VALUES

(1, '健康异常', '警告', '心率异常：检测到心率过高', '2024-01-15 10:30:00',
 '{"heart_rate": 135, "threshold": 120}', '已处理', '护士小王',
 '2024-01-15 10:35:00', '已联系家属，老人情况稳定', '老人刚运动完，心率正常升高',
 '2024-01-15 10:30:00', '2024-01-15 10:35:00'),

(2, '健康异常', '严重', '血压异常：收缩压过高', '2024-01-15 11:15:00',
 '{"systolic": 165, "diastolic": 95, "threshold_systolic": 140}', '已处理', '医生李明',
 '2024-01-15 11:20:00', '建议调整药物剂量，已联系主治医生', '需要密切观察血压变化',
 '2024-01-15 11:15:00', '2024-01-15 11:20:00');

-- 10. 插入操作日志数据
INSERT INTO `operation_log` (`username`, `user_id`, `operation_type`, `operation_desc`, `module`, `log_level`, `ip_address`, `user_agent`, `request_url`, `request_method`, `request_params`, `response_data`, `request_time`) VALUES
('admin', 1, 'CREATE', '创建了新用户 "张三"', '用户管理', 'INFO', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/system-users', 'POST', '{"username":"zhangsan","fullName":"张三"}', '{"success":true,"data":{"id":1}}', 156),
('admin', 1, 'LOGIN', '管理员登录系统', '用户认证', 'INFO', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/auth/login', 'POST', '{"username":"admin"}', '{"success":true,"token":"xxx"}', 78);

-- 插入完成提示
SELECT 'Database schema and initial data created successfully!' as result; 

-- ============================
-- 机构负责人角色管理功能数据库脚本
-- 执行时间：2025-01-25
-- ============================

-- 1. 创建机构负责人角色
INSERT INTO `role` (`role_name`, `role_key`, `description`) VALUES 
('机构负责人', 'ORG_DIRECTOR', '机构负责人，负责机构日常管理和运营');

-- 2. 创建机构负责人用户账号
INSERT INTO `system_user` (`username`, `password_hash`, `full_name`, `email`, `phone`, `organization_id`, `is_admin`, `is_active`) VALUES 
-- 阳光老年公寓负责人
('zhangming', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '张明', 'zhangming@yangguang.com', '13901234567', 1, FALSE, TRUE),
-- 和谐养老院负责人  
('lihua', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '李华', 'lihua@hexie.com', '13812345678', 2, FALSE, TRUE),
-- 乐龄社区日照中心负责人
('wangfang', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '王芳', 'wangfang@leling.com', '13923456789', 3, FALSE, TRUE),
-- 其他机构负责人（备用）
('chenwei', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '陈伟', 'chenwei@example.com', '13701234567', 1, FALSE, TRUE),
('liujing', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '刘静', 'liujing@example.com', '13801234567', 2, FALSE, TRUE);

-- 3. 为机构负责人分配角色
-- 首先获取机构负责人角色ID（假设为4，如果不是请根据实际情况调整）
SET @director_role_id = (SELECT id FROM `role` WHERE `role_key` = 'ORG_DIRECTOR');

-- 为所有机构负责人分配角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES 
((SELECT id FROM `system_user` WHERE `username` = 'zhangming'), @director_role_id),
((SELECT id FROM `system_user` WHERE `username` = 'lihua'), @director_role_id),
((SELECT id FROM `system_user` WHERE `username` = 'wangfang'), @director_role_id),
((SELECT id FROM `system_user` WHERE `username` = 'chenwei'), @director_role_id),
((SELECT id FROM `system_user` WHERE `username` = 'liujing'), @director_role_id);

-- 4. 验证数据插入结果
SELECT 
    '=== 角色信息 ===' as info,
    r.id as role_id,
    r.role_name,
    r.role_key,
    r.description
FROM `role` r 
WHERE r.role_key = 'ORG_DIRECTOR';

SELECT 
    '=== 机构负责人用户信息 ===' as info,
    u.id as user_id,
    u.username,
    u.full_name,
    u.email,
    u.phone,
    o.name as organization_name,
    r.role_name
FROM `system_user` u
LEFT JOIN `organization` o ON u.organization_id = o.id
LEFT JOIN `user_role` ur ON u.id = ur.user_id
LEFT JOIN `role` r ON ur.role_id = r.id
WHERE r.role_key = 'ORG_DIRECTOR'
ORDER BY u.organization_id, u.full_name;

-- 5. 创建查询机构负责人的视图（可选，便于前端查询）
CREATE OR REPLACE VIEW `v_organization_directors` AS
SELECT 
    u.id as user_id,
    u.username,
    u.full_name as director_name,
    u.phone as director_contact,
    u.email,
    u.organization_id,
    o.name as organization_name,
    o.short_name as organization_short_name
FROM `system_user` u
INNER JOIN `user_role` ur ON u.id = ur.user_id
INNER JOIN `role` r ON ur.role_id = r.id
LEFT JOIN `organization` o ON u.organization_id = o.id
WHERE r.role_key = 'ORG_DIRECTOR' 
  AND u.is_active = TRUE
ORDER BY u.organization_id, u.full_name;

-- 6. 测试查询视图
SELECT 
    '=== 机构负责人视图测试 ===' as info,
    user_id,
    director_name,
    director_contact,
    organization_name
FROM `v_organization_directors`;

-- 脚本执行完成提示
SELECT 
    '=== 脚本执行完成 ===' as status,
    '已创建机构负责人角色和用户，请检查上述查询结果' as message,
    NOW() as execution_time; 

-- 插入所属社区字典数据
INSERT INTO `dictionary` (`dict_type`, `dict_code`, `dict_label`, `dict_value`, `sort_order`, `status`, `remark`) VALUES
('community', 'CHAOYANGPARK', '朝阳公园社区', '朝阳公园社区', 1, 'ACTIVE', '朝阳区朝阳公园社区'),
('community', 'ZHONGGUANCUN', '中关村社区', '中关村社区', 2, 'ACTIVE', '海淀区中关村社区'),
('community', 'XIDAN', '西单社区', '西单社区', 3, 'ACTIVE', '西城区西单社区'),
('community', 'WANGFUJING', '王府井社区', '王府井社区', 4, 'ACTIVE', '东城区王府井社区'),
('community', 'SANLITUN', '三里屯社区', '三里屯社区', 5, 'ACTIVE', '朝阳区三里屯社区'),
('community', 'ZHONGSHAN', '中山社区', '中山社区', 6, 'ACTIVE', '西城区中山社区'),
('community', 'DONGZHIMEN', '东直门社区', '东直门社区', 7, 'ACTIVE', '东城区东直门社区'),
('community', 'HAIDIAN', '海淀社区', '海淀社区', 8, 'ACTIVE', '海淀区海淀社区'),
('community', 'FENGTAI', '丰台社区', '丰台社区', 9, 'ACTIVE', '丰台区丰台社区'),
('community', 'SHIJINGSHAN', '石景山社区', '石景山社区', 10, 'ACTIVE', '石景山区石景山社区');

-- 插入养老类型字典数据
INSERT INTO `dictionary` (`dict_type`, `dict_code`, `dict_label`, `dict_value`, `sort_order`, `status`, `remark`) VALUES
('pensionType', 'HOME_CARE', '居家养老', '居家养老', 1, 'ACTIVE', '在家中接受养老服务'),
('pensionType', 'COMMUNITY_DAY_CARE', '社区养老（日照）', '社区养老（日照）', 2, 'ACTIVE', '社区日间照料中心'),
('pensionType', 'INSTITUTIONAL_CARE', '机构养老（养老院）', '机构养老（养老院）', 3, 'ACTIVE', '入住养老机构'),
('pensionType', 'MEDICAL_CARE', '医养结合', '医养结合', 4, 'ACTIVE', '医疗与养老相结合'),
('pensionType', 'ASSISTED_LIVING', '辅助生活', '辅助生活', 5, 'ACTIVE', '半独立生活方式'),
('pensionType', 'RESPITE_CARE', '临时照护', '临时照护', 6, 'ACTIVE', '短期临时照护服务'),
('pensionType', 'HOSPICE_CARE', '临终关怀', '临终关怀', 7, 'ACTIVE', '生命末期关怀服务'),
('pensionType', 'SMART_ELDERLY', '智慧养老', '智慧养老', 8, 'ACTIVE', '基于智能技术的养老服务');

-- 插入服务类型字典数据
INSERT INTO `dictionary` (`dict_type`, `dict_code`, `dict_label`, `dict_value`, `sort_order`, `status`, `remark`) VALUES
('serviceType', 'LIFE_CARE', '生活照料', '生活照料', 1, 'ACTIVE', '日常生活照料服务'),
('serviceType', 'MEDICAL_CARE', '医疗护理', '医疗护理', 2, 'ACTIVE', '医疗护理服务'),
('serviceType', 'REHABILITATION', '康复训练', '康复训练', 3, 'ACTIVE', '康复训练服务'),
('serviceType', 'PSYCHOLOGICAL_SUPPORT', '心理支持', '心理支持', 4, 'ACTIVE', '心理疏导和支持服务'),
('serviceType', 'ENTERTAINMENT', '文娱活动', '文娱活动', 5, 'ACTIVE', '文化娱乐活动服务'),
('serviceType', 'EMERGENCY_RESCUE', '紧急救援', '紧急救援', 6, 'ACTIVE', '紧急救援服务'),
('serviceType', 'HOUSEKEEPING', '家政服务', '家政服务', 7, 'ACTIVE', '家政清洁服务'),
('serviceType', 'MEAL_DELIVERY', '送餐服务', '送餐服务', 8, 'ACTIVE', '餐饮配送服务'),
('serviceType', 'TRANSPORTATION', '交通接送', '交通接送', 9, 'ACTIVE', '交通接送服务'),
('serviceType', 'CONSULTATION', '咨询服务', '咨询服务', 10, 'ACTIVE', '各类咨询服务'); 

-- 菜单权限数据初始化脚本
-- 对应顶部菜单栏的菜单结构

-- 清空现有菜单权限数据
DELETE FROM `role_permission`;
DELETE FROM `menu_permission`;

-- 重置自增ID
ALTER TABLE `menu_permission` AUTO_INCREMENT = 1;

-- 插入顶级菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 1. 首页
(1, NULL, '首页', 'MENU', 'home:view', '/', 'HomeView', 'House', 1, TRUE, TRUE),

-- 2. 机构管理
(2, NULL, '机构管理', 'MENU', 'organization:view', '/organization-management', 'views/organization/OrganizationIndex', 'OfficeBuilding', 2, TRUE, TRUE),

-- 3. 人员档案
(3, NULL, '人员档案', 'MENU', 'elderly:view', '/elderly-profiles', 'views/elderly/ElderlyProfileList', 'User', 3, TRUE, TRUE),

-- 4. 智能设备（目录）
(4, NULL, '智能设备', 'CATALOG', 'smart-device:view', '/smart-device', NULL, 'Monitor', 4, TRUE, TRUE),

-- 5. 服务记录
(5, NULL, '服务记录', 'MENU', 'service:view', '/service-records', 'views/service-record/ServiceRecordList', 'Document', 5, TRUE, TRUE),

-- 6. 志愿者管理
(6, NULL, '志愿者管理', 'MENU', 'volunteer:view', '/volunteers', 'views/volunteer/VolunteerListSimple', 'Avatar', 6, TRUE, TRUE),

-- 7. 系统管理（目录）
(7, NULL, '系统管理', 'CATALOG', 'system:view', '/system', NULL, 'Setting', 7, TRUE, TRUE);

-- 插入智能设备子菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 智能设备子菜单
(11, 4, '设备管理', 'MENU', 'smart-device:manage', '/smart-devices', 'views/smart-device/SmartDeviceList', 'Monitor', 1, TRUE, TRUE),
(12, 4, '告警管理', 'MENU', 'smart-device:alarm', '/device-alarms', 'views/smart-device/DeviceAlarmList', 'Warning', 2, TRUE, TRUE);

-- 插入系统管理子菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 系统管理子菜单
(21, 7, '用户管理', 'MENU', 'system:user:manage', '/system/users', 'views/system/SystemUserList', 'User', 1, TRUE, TRUE),
(22, 7, '角色管理', 'MENU', 'system:role:manage', '/system/roles', 'views/system/RoleList', 'Avatar', 2, TRUE, TRUE),
(23, 7, '权限管理', 'MENU', 'system:permission:manage', '/system/permissions', 'views/system/PermissionList', 'Key', 3, TRUE, TRUE),
(24, 7, '菜单管理', 'MENU', 'system:menu:manage', '/system/menus', 'views/system/MenuList', 'Menu', 4, TRUE, TRUE),
(25, 7, '日志管理', 'MENU', 'system:log:manage', '/system/logs', 'views/system/LogList', 'Document', 5, TRUE, TRUE),
(26, 7, '字典管理', 'MENU', 'system:dict:manage', '/system/dictionaries', 'views/system/DictionaryManagementSimple', 'Collection', 6, TRUE, TRUE);

-- 插入按钮权限（针对各个功能模块）
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 机构管理按钮权限
(101, 2, '新增机构', 'BUTTON', 'organization:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(102, 2, '编辑机构', 'BUTTON', 'organization:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(103, 2, '删除机构', 'BUTTON', 'organization:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(104, 2, '导出机构', 'BUTTON', 'organization:export', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 人员档案按钮权限
(111, 3, '新增档案', 'BUTTON', 'elderly:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(112, 3, '编辑档案', 'BUTTON', 'elderly:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(113, 3, '删除档案', 'BUTTON', 'elderly:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(114, 3, '导出档案', 'BUTTON', 'elderly:export', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 智能设备按钮权限
(121, 11, '新增设备', 'BUTTON', 'smart-device:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(122, 11, '编辑设备', 'BUTTON', 'smart-device:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(123, 11, '删除设备', 'BUTTON', 'smart-device:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(124, 12, '处理告警', 'BUTTON', 'smart-device:alarm:handle', NULL, NULL, NULL, 1, FALSE, TRUE),

-- 服务记录按钮权限
(131, 5, '新增记录', 'BUTTON', 'service:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(132, 5, '编辑记录', 'BUTTON', 'service:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(133, 5, '删除记录', 'BUTTON', 'service:delete', NULL, NULL, NULL, 3, FALSE, TRUE),

-- 志愿者管理按钮权限
(141, 6, '新增志愿者', 'BUTTON', 'volunteer:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(142, 6, '编辑志愿者', 'BUTTON', 'volunteer:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(143, 6, '删除志愿者', 'BUTTON', 'volunteer:delete', NULL, NULL, NULL, 3, FALSE, TRUE),

-- 用户管理按钮权限
(151, 21, '新增用户', 'BUTTON', 'system:user:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(152, 21, '编辑用户', 'BUTTON', 'system:user:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(153, 21, '删除用户', 'BUTTON', 'system:user:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(154, 21, '重置密码', 'BUTTON', 'system:user:reset-password', NULL, NULL, NULL, 4, FALSE, TRUE),
(155, 21, '分配角色', 'BUTTON', 'system:user:assign-role', NULL, NULL, NULL, 5, FALSE, TRUE),

-- 角色管理按钮权限
(161, 22, '新增角色', 'BUTTON', 'system:role:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(162, 22, '编辑角色', 'BUTTON', 'system:role:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(163, 22, '删除角色', 'BUTTON', 'system:role:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(164, 22, '分配权限', 'BUTTON', 'system:role:assign-permission', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 菜单管理按钮权限
(171, 24, '新增菜单', 'BUTTON', 'system:menu:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(172, 24, '编辑菜单', 'BUTTON', 'system:menu:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(173, 24, '删除菜单', 'BUTTON', 'system:menu:delete', NULL, NULL, NULL, 3, FALSE, TRUE);

-- 为超级管理员角色分配所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `menu_permission` WHERE `status` = TRUE;

-- 为机构管理员角色分配基本权限（排除系统管理）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `menu_permission` 
WHERE `status` = TRUE 
AND id NOT IN (7, 21, 22, 23, 24, 25, 26, 151, 152, 153, 154, 155, 161, 162, 163, 164, 171, 172, 173);

-- 为普通用户角色分配查看权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `menu_permission` 
WHERE `status` = TRUE 
AND `type` IN ('MENU', 'CATALOG')
AND id NOT IN (7, 21, 22, 23, 24, 25, 26);

-- 验证插入结果
SELECT 
    '=== 菜单权限数据插入完成 ===' as status,
    (SELECT COUNT(*) FROM `menu_permission`) as total_permissions,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'CATALOG') as catalogs,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'MENU') as menus,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'BUTTON') as buttons,
    (SELECT COUNT(*) FROM `role_permission`) as role_permissions;

-- 显示菜单树结构
SELECT 
    CASE 
        WHEN parent_id IS NULL THEN CONCAT('├─ ', name)
        ELSE CONCAT('│  ├─ ', name)
    END as menu_structure,
    type,
    permission_key,
    route_path
FROM `menu_permission` 
WHERE `status` = TRUE
ORDER BY 
    COALESCE(parent_id, id), 
    sort_order; 