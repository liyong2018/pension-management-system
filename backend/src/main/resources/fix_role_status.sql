-- 修复角色表缺少status字段的问题
-- 添加status字段到role表
ALTER TABLE `role` ADD COLUMN `status` VARCHAR(1) DEFAULT '1' COMMENT '状态：1启用，0禁用';

-- 更新现有角色的状态为启用
UPDATE `role` SET `status` = '1' WHERE `status` IS NULL; 