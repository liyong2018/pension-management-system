-- 为老人档案表添加老人类型字段
-- 执行时间：2025-01-26

-- 1. 添加老人类型字段
ALTER TABLE `elderly_profile` 
ADD COLUMN `elderly_type` VARCHAR(50) COMMENT '老人类型（字典值）' AFTER `pension_type`;

-- 2. 为老人类型字段添加索引
CREATE INDEX `idx_elderly_type` ON `elderly_profile`(`elderly_type`);

-- 3. 插入老人类型字典数据
INSERT INTO `dictionary` (`dict_type`, `dict_code`, `dict_label`, `dict_value`, `sort_order`, `status`, `remark`) VALUES
('elderly_type', 'normal', '普通老人', 'normal', 1, 'ACTIVE', '身体健康，生活能够自理的老人'),
('elderly_type', 'empty_nest', '空巢老人', 'empty_nest', 2, 'ACTIVE', '子女长期不在身边的老人'),
('elderly_type', 'living_alone', '独居老人', 'living_alone', 3, 'ACTIVE', '独自居住的老人'),
('elderly_type', 'disabled', '失能老人', 'disabled', 4, 'ACTIVE', '生活不能自理的老人'),
('elderly_type', 'elderly', '高龄老人', 'elderly', 5, 'ACTIVE', '80岁以上的老人'),
('elderly_type', 'low_income', '低收入老人', 'low_income', 6, 'ACTIVE', '经济困难的老人'),
('elderly_type', 'special_care', '特殊照护老人', 'special_care', 7, 'ACTIVE', '需要特殊照护的老人');

-- 4. 根据现有数据更新老人类型（示例逻辑）
-- 根据能力评估和年龄等信息推断老人类型
UPDATE `elderly_profile` 
SET `elderly_type` = CASE 
    WHEN `ability_assessment` = '轻度失能' OR `ability_assessment` = '中度失能' OR `ability_assessment` = '重度失能' THEN 'disabled'
    WHEN TIMESTAMPDIFF(YEAR, `birth_date`, CURDATE()) >= 80 THEN 'elderly'
    WHEN `pension_type` = '居家养老' AND `remarks` LIKE '%独居%' THEN 'living_alone'
    WHEN `pension_type` = '居家养老' AND `remarks` LIKE '%空巢%' THEN 'empty_nest'
    WHEN `remarks` LIKE '%低收入%' OR `remarks` LIKE '%困难%' THEN 'low_income'
    ELSE 'normal'
END
WHERE `elderly_type` IS NULL;

-- 5. 验证更新结果
SELECT 
    '=== 老人类型统计 ===' as info,
    elderly_type,
    COUNT(*) as count
FROM `elderly_profile` 
WHERE `elderly_type` IS NOT NULL
GROUP BY `elderly_type`
ORDER BY `elderly_type`;

SELECT 'Migration completed successfully!' as result; 