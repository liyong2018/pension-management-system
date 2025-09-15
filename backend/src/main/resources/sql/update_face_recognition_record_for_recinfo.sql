-- 更新face_recognition_record表结构以支持RecInfo.java中的所有字段
-- 执行时间：2025-01-16
-- 目的：确保能够完整存储人脸识别设备推送的所有数据

-- 添加RecInfo.java中缺失的字段
ALTER TABLE face_recognition_record 
-- 基本人员信息字段
ADD COLUMN customize_id INT COMMENT '自定义ID' AFTER custom_id,
ADD COLUMN person_uuid VARCHAR(100) COMMENT '人员UUID' AFTER person_id,
ADD COLUMN create_time_device VARCHAR(50) COMMENT '设备创建时间' AFTER person_uuid,
ADD COLUMN name VARCHAR(100) COMMENT '人员姓名' AFTER create_time_device,
ADD COLUMN gender INT COMMENT '性别：0=男，1=女' AFTER name,
ADD COLUMN nation INT COMMENT '民族' AFTER gender,
ADD COLUMN card_type INT COMMENT '证件类型' AFTER nation,
ADD COLUMN birthday VARCHAR(20) COMMENT '生日' AFTER id_card,
ADD COLUMN native_place VARCHAR(100) COMMENT '籍贯' AFTER telnum,
ADD COLUMN address VARCHAR(255) COMMENT '地址' AFTER native_place,
ADD COLUMN notes TEXT COMMENT '备注' AFTER address,

-- 认证相关字段
ADD COLUMN record_id_device BIGINT COMMENT '设备记录ID（断点续传用）' AFTER notes,
ADD COLUMN verify_type INT COMMENT '认证类型' AFTER verify_status,

-- 卡片相关字段
ADD COLUMN mj_card_from VARCHAR(50) COMMENT '卡来源' AFTER similarity2,
ADD COLUMN mj_card_no VARCHAR(50) COMMENT '卡号' AFTER mj_card_from,
ADD COLUMN temp_valid INT COMMENT '临时有效' AFTER mj_card_no,
ADD COLUMN valid_begin VARCHAR(50) COMMENT '有效开始时间' AFTER temp_valid,
ADD COLUMN valid_end VARCHAR(50) COMMENT '有效结束时间' AFTER valid_begin,

-- 设备和位置信息
ADD COLUMN facesluice_id VARCHAR(100) COMMENT '闸机设备ID' AFTER facesluice_name,
ADD COLUMN recognition_time VARCHAR(50) COMMENT '识别时间' AFTER face_bottom,
ADD COLUMN opendoor_way VARCHAR(50) COMMENT '开门方式' AFTER push_type,

-- 图片和文件信息
ADD COLUMN sanp_pic LONGTEXT COMMENT '抓拍图片Base64编码（人脸识别图片）' AFTER dw_file_pos,
ADD COLUMN pic LONGTEXT COMMENT '抓拍图片Base64编码（通用字段）' AFTER sanp_pic;

-- 添加索引以提高查询性能
ALTER TABLE face_recognition_record 
ADD INDEX idx_customize_id (customize_id),
ADD INDEX idx_person_uuid (person_uuid),
ADD INDEX idx_name (name),
ADD INDEX idx_gender (gender),
ADD INDEX idx_birthday (birthday),
ADD INDEX idx_record_id_device (record_id_device),
ADD INDEX idx_verify_type (verify_type),
ADD INDEX idx_facesluice_id (facesluice_id),
ADD INDEX idx_recognition_time (recognition_time);

-- 验证表结构更新
SELECT 'face_recognition_record表结构更新完成，现在支持RecInfo.java中的所有字段' as status;

-- 显示更新后的表结构
DESC face_recognition_record;