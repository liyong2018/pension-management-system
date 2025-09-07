-- 为face_recognition_record表添加新字段以支持完整的人脸识别数据
-- 执行时间：2025-09-07

ALTER TABLE face_recognition_record 
ADD COLUMN person_id VARCHAR(100) COMMENT '人员ID' AFTER custom_id,
ADD COLUMN person_type INT COMMENT '人员类型：0=白名单，1=黑名单' AFTER verify_status,
ADD COLUMN similarity2 DECIMAL(8,6) COMMENT '人脸相似度2' AFTER similarity,
ADD COLUMN sendintime INT COMMENT '发送时间标识' AFTER similarity2,
ADD COLUMN direction VARCHAR(20) COMMENT '通行方向：enter=进入，exit=退出' AFTER sendintime,
ADD COLUMN otype VARCHAR(20) COMMENT '操作类型' AFTER direction,
ADD COLUMN persion_name VARCHAR(100) COMMENT '人员姓名（拼写错误版本，保持兼容）' AFTER otype,
ADD COLUMN person_name VARCHAR(100) COMMENT '人员姓名（正确拼写）' AFTER persion_name,
ADD COLUMN facesluice_name VARCHAR(100) COMMENT '闸机设备名称' AFTER person_name,
ADD COLUMN id_card VARCHAR(50) COMMENT '身份证号' AFTER facesluice_name,
ADD COLUMN telnum VARCHAR(20) COMMENT '电话号码' AFTER id_card,
ADD COLUMN face_left VARCHAR(10) COMMENT '人脸框左边界' AFTER telnum,
ADD COLUMN face_top VARCHAR(10) COMMENT '人脸框上边界' AFTER face_left,
ADD COLUMN face_right VARCHAR(10) COMMENT '人脸框右边界' AFTER face_top,
ADD COLUMN face_bottom VARCHAR(10) COMMENT '人脸框下边界' AFTER face_right,
ADD COLUMN push_type VARCHAR(10) COMMENT '推送类型' AFTER face_bottom,
ADD COLUMN opendoor_way VARCHAR(10) COMMENT '开门方式' AFTER push_type,
ADD COLUMN card_num2 VARCHAR(50) COMMENT '卡号2' AFTER opendoor_way,
ADD COLUMN rfid_card VARCHAR(50) COMMENT 'RFID卡号' AFTER card_num2,
ADD COLUMN sz_qr_code_data TEXT COMMENT '二维码数据' AFTER rfid_card,
ADD COLUMN is_no_mask VARCHAR(10) COMMENT '是否戴口罩：0=戴口罩，1=未戴口罩' AFTER sz_qr_code_data,
ADD COLUMN dw_file_index VARCHAR(20) COMMENT '文件索引' AFTER is_no_mask,
ADD COLUMN dw_file_pos VARCHAR(20) COMMENT '文件位置' AFTER dw_file_index;

-- 添加索引以提高查询性能
ALTER TABLE face_recognition_record 
ADD INDEX idx_person_id (person_id),
ADD INDEX idx_person_type (person_type),
ADD INDEX idx_direction (direction),
ADD INDEX idx_person_name (person_name),
ADD INDEX idx_id_card (id_card);

-- 更新字段注释
ALTER TABLE face_recognition_record 
MODIFY COLUMN similarity DECIMAL(8,6) COMMENT '人脸相似度1';