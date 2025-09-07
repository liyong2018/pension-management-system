-- 创建智能设备表
CREATE TABLE IF NOT EXISTS `smart_device` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `device_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '设备编号',
    `device_name` VARCHAR(255) NOT NULL COMMENT '设备名称',
    `device_type` VARCHAR(100) COMMENT '设备类型',
    `device_brand` VARCHAR(100) COMMENT '设备品牌',
    `device_model` VARCHAR(100) COMMENT '设备型号',
    `device_status` VARCHAR(50) DEFAULT '在线' COMMENT '设备状态',
    `purchase_date` DATE COMMENT '购买日期',
    `installation_location` VARCHAR(255) COMMENT '安装位置',
    `elderly_id` BIGINT COMMENT '关联老人ID',
    `organization_id` BIGINT COMMENT '所属机构ID',
    
    -- 配置信息
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `mac_address` VARCHAR(50) COMMENT 'MAC地址',
    `wifi_config` TEXT COMMENT 'WiFi配置',
    `communication_protocol` VARCHAR(100) COMMENT '通信协议',
    `data_collection_frequency` INT DEFAULT 60 COMMENT '数据采集频率(秒)',
    `alarm_threshold` VARCHAR(255) COMMENT '报警阈值',
    `battery_threshold` INT DEFAULT 20 COMMENT '电池阈值(%)',
    
    -- 监控信息
    `real_time_data` TEXT COMMENT '实时数据',
    `battery_level` INT COMMENT '电池电量(%)',
    `signal_strength` INT COMMENT '信号强度',
    `last_communication_time` DATETIME COMMENT '最后通信时间',
    `data_upload_status` VARCHAR(50) DEFAULT '正常' COMMENT '数据上传状态',
    
    -- 维护信息
    `warranty_expiry_date` DATE COMMENT '保修到期日期',
    `maintenance_cycle` INT COMMENT '维护周期(天)',
    `next_maintenance_date` DATE COMMENT '下次维护日期',
    `failure_count` INT DEFAULT 0 COMMENT '故障次数',
    
    -- 系统字段
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) COMMENT '创建人',
    `updated_by` VARCHAR(100) COMMENT '更新人',
    `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX `idx_device_code` (`device_code`),
    INDEX `idx_device_type` (`device_type`),
    INDEX `idx_device_status` (`device_status`),
    INDEX `idx_elderly_id` (`elderly_id`),
    INDEX `idx_organization_id` (`organization_id`),
    INDEX `idx_ip_address` (`ip_address`),
    INDEX `idx_last_communication_time` (`last_communication_time`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能设备表';

SELECT 'Smart device table created successfully!' as status;