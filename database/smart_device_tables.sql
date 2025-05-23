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
    
    -- 配置信息
    ip_address VARCHAR(50) COMMENT 'IP地址',
    mac_address VARCHAR(50) COMMENT 'MAC地址',
    wifi_config TEXT COMMENT 'Wi-Fi配置信息',
    communication_protocol VARCHAR(50) COMMENT '通信协议：TCP/IP/MQTT/LoRa等',
    data_collection_frequency INT DEFAULT 60 COMMENT '数据采集频率（秒）',
    alarm_threshold TEXT COMMENT '报警阈值配置（JSON格式）',
    battery_threshold INT DEFAULT 20 COMMENT '电量阈值（百分比）',
    
    -- 监控信息
    real_time_data TEXT COMMENT '实时数据（JSON格式）',
    battery_level INT COMMENT '电量百分比',
    signal_strength INT COMMENT '信号强度',
    last_communication_time DATETIME COMMENT '最后通信时间',
    data_upload_status VARCHAR(20) DEFAULT '正常' COMMENT '数据上传状态：正常/异常/延迟',
    
    -- 维护信息
    warranty_expiry_date DATE COMMENT '保修期限',
    maintenance_cycle INT COMMENT '维护周期（天）',
    next_maintenance_date DATE COMMENT '下次维护时间',
    failure_count INT DEFAULT 0 COMMENT '故障次数',
    
    -- 系统字段
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
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL
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
    
    -- 处理信息
    process_status VARCHAR(20) DEFAULT '未处理' COMMENT '处理状态：未处理/处理中/已处理/已忽略',
    process_person VARCHAR(100) COMMENT '处理人员',
    process_time DATETIME COMMENT '处理时间',
    process_result TEXT COMMENT '处理结果描述',
    remarks TEXT COMMENT '备注',
    
    -- 系统字段
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
    
    -- 系统字段
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
    
    -- 系统字段
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