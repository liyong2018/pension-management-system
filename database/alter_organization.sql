USE pension_management_system;

-- 添加 description 字段
ALTER TABLE organization ADD COLUMN description TEXT COMMENT '机构描述' AFTER other_qualifications; 