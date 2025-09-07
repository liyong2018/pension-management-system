-- 创建人脸识别相关表

-- 人脸识别记录表
CREATE TABLE IF NOT EXISTS face_recognition_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    custom_id VARCHAR(100) COMMENT '人员自定义ID',
    record_id BIGINT COMMENT '识别记录ID',
    verify_status INT COMMENT '验证状态：1=允许，2=拒绝，22=待核验',
    similarity DECIMAL(5,2) COMMENT '相似度百分比',
    image_path VARCHAR(500) COMMENT '人脸图片存储路径',
    recognition_time DATETIME COMMENT '识别时间',
    temperature DECIMAL(4,1) COMMENT '体温',
    device_id VARCHAR(100) COMMENT '设备ID',
    elderly_id BIGINT COMMENT '老人档案ID',
    organization_id BIGINT COMMENT '机构ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_custom_id (custom_id),
    INDEX idx_device_id (device_id),
    INDEX idx_recognition_time (recognition_time),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_organization_id (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸识别记录表';

-- 陌生人抓拍记录表
CREATE TABLE IF NOT EXISTS face_stranger_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    snap_id VARCHAR(100) COMMENT '抓拍ID',
    snap_time DATETIME COMMENT '抓拍时间',
    image_path VARCHAR(500) COMMENT '抓拍图片存储路径',
    device_id VARCHAR(100) COMMENT '设备ID',
    organization_id BIGINT COMMENT '机构ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_snap_id (snap_id),
    INDEX idx_device_id (device_id),
    INDEX idx_snap_time (snap_time),
    INDEX idx_organization_id (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陌生人抓拍记录表';