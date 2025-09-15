# face_recognition_record 表结构更新总结

## 更新目的
根据 `RecInfo.java` 中定义的字段，更新 `face_recognition_record` 表结构，确保能够完整存储人脸识别设备推送的所有数据。

## 更新时间
2025-01-16

## 新增字段列表

### 基本人员信息字段
- `customize_id` INT - 自定义ID (对应RecInfo.customizeId)
- `person_uuid` VARCHAR(100) - 人员UUID (对应RecInfo.personUUID)
- `create_time_device` VARCHAR(50) - 设备创建时间 (对应RecInfo.createTime)
- `name` VARCHAR(100) - 人员姓名 (对应RecInfo.name)
- `gender` INT - 性别：0=男，1=女 (对应RecInfo.gender)
- `nation` INT - 民族 (对应RecInfo.nation)
- `card_type` INT - 证件类型 (对应RecInfo.cardType)
- `birthday` VARCHAR(20) - 生日 (对应RecInfo.birthday)
- `native_place` VARCHAR(100) - 籍贯 (对应RecInfo.nativePlace)
- `address` VARCHAR(255) - 地址 (对应RecInfo.address)
- `notes` TEXT - 备注 (对应RecInfo.notes)

### 认证相关字段
- `record_id_device` BIGINT - 设备记录ID (对应RecInfo.recordID)
- `verify_type` INT - 认证类型 (对应RecInfo.verfyType)

### 卡片相关字段
- `mj_card_from` VARCHAR(50) - 卡来源 (对应RecInfo.mjCardFrom)
- `mj_card_no` VARCHAR(50) - 卡号 (对应RecInfo.mjCardNo)
- `temp_valid` INT - 临时有效 (对应RecInfo.tempvalid)
- `valid_begin` VARCHAR(50) - 有效开始时间 (对应RecInfo.validBegin)
- `valid_end` VARCHAR(50) - 有效结束时间 (对应RecInfo.validEnd)

### 设备和位置信息
- `facesluice_id` VARCHAR(100) - 闸机设备ID (对应RecInfo.facesluiceId)

### 图片和文件信息
- `sanp_pic` LONGTEXT - 抓拍图片Base64编码 (对应RecInfo.sanpPic)
- `pic` LONGTEXT - 抓拍图片Base64编码通用字段 (对应RecInfo.pic)

## 新增索引
为提高查询性能，添加了以下索引：
- `idx_customize_id` - 自定义ID索引
- `idx_person_uuid` - 人员UUID索引
- `idx_name` - 姓名索引
- `idx_gender` - 性别索引
- `idx_birthday` - 生日索引
- `idx_record_id_device` - 设备记录ID索引
- `idx_verify_type` - 认证类型索引
- `idx_facesluice_id` - 闸机设备ID索引

## 表结构统计
- **更新前字段数量**: 约35个字段
- **更新后字段数量**: 57个字段
- **新增字段数量**: 22个字段

## 已存在的字段
以下RecInfo.java中的字段在表中已经存在，无需重复添加：
- `custom_id` - 人员唯一ID
- `person_id` - 人员ID
- `verify_status` - 认证结果
- `person_type` - 人员类型
- `similarity` - 人脸相似度1
- `similarity2` - 人脸相似度2
- `sendintime` - 发送时间标识
- `direction` - 通行方向
- `otype` - 操作类型
- `persion_name` - 人员姓名（拼写错误版本）
- `person_name` - 人员姓名（正确拼写）
- `facesluice_name` - 闸机设备名称
- `id_card` - 身份证号
- `telnum` - 电话号码
- `face_left`, `face_top`, `face_right`, `face_bottom` - 人脸框坐标
- `push_type` - 推送类型
- `opendoor_way` - 开门方式
- `card_num2` - 卡号2
- `rfid_card` - RFID卡号
- `sz_qr_code_data` - 二维码数据
- `is_no_mask` - 是否戴口罩
- `dw_file_index` - 文件索引
- `dw_file_pos` - 文件位置
- `recognition_time` - 识别时间
- `temperature` - 体温
- `device_id` - 设备ID

## 使用说明
现在 `face_recognition_record` 表可以完整存储 `RecInfo.java` 中定义的所有字段数据。在 `FaceRecognitionServiceImpl.processRecognitionRecord` 方法中，可以直接将 RecInfo 对象的所有字段映射到数据库记录中。

## 相关文件
- 更新脚本: `update_face_recognition_record_missing_fields.sql`
- 实体类: `com.example.pension.model.face.RecInfo.java`
- 数据库表: `face_recognition_record`