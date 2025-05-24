-- 修复服务记录表中的状态值映射
USE pension_management_system;

-- 将英文状态值更新为中文状态值
UPDATE service_record 
SET status = CASE 
    WHEN status = 'Pending' THEN '待处理'
    WHEN status = 'In Progress' THEN '进行中'
    WHEN status = 'Completed' THEN '已完成'
    WHEN status = 'Evaluated' THEN '已评价'
    ELSE status
END
WHERE status IN ('Pending', 'In Progress', 'Completed', 'Evaluated');

-- 将英文服务提供者类型更新为中文
UPDATE service_record 
SET service_provider_type = CASE 
    WHEN service_provider_type = 'Staff' THEN '机构员工'
    WHEN service_provider_type = 'Volunteer' THEN '志愿者'
    WHEN service_provider_type = 'Outsourced' THEN '外包服务'
    WHEN service_provider_type = 'Housekeeping' THEN '家政服务'
    ELSE service_provider_type
END
WHERE service_provider_type IN ('Staff', 'Volunteer', 'Outsourced', 'Housekeeping');

-- 将英文服务内容更新为中文示例
UPDATE service_record 
SET service_content = CASE 
    WHEN service_content = 'Daily care service' THEN '日常生活护理服务'
    WHEN service_content = 'Health check service' THEN '健康检查服务'
    WHEN service_content = 'Medical consultation' THEN '医疗咨询服务'
    ELSE service_content
END
WHERE service_content IN ('Daily care service', 'Health check service', 'Medical consultation');

-- 将英文服务地址更新为中文
UPDATE service_record 
SET service_address = CASE 
    WHEN service_address = 'Beijing Chaoyang District' THEN '北京市朝阳区'
    WHEN service_address = 'Beijing Haidian District' THEN '北京市海淀区'
    WHEN service_address = 'Beijing Chaoyang Nursing Home' THEN '北京朝阳养老院'
    ELSE service_address
END
WHERE service_address IN ('Beijing Chaoyang District', 'Beijing Haidian District', 'Beijing Chaoyang Nursing Home');

-- 将英文服务提供者姓名更新为中文
UPDATE service_record 
SET service_provider_name = CASE 
    WHEN service_provider_name = 'Zhang Nurse' THEN '张护士'
    WHEN service_provider_name = 'Li Doctor' THEN '李医生'
    WHEN service_provider_name = 'Wang Nurse' THEN '王护士'
    ELSE service_provider_name
END
WHERE service_provider_name IN ('Zhang Nurse', 'Li Doctor', 'Wang Nurse');

-- 查看更新后的数据
SELECT id, elderly_id, service_content, service_provider_type, service_provider_name, status, service_time 
FROM service_record 
ORDER BY service_time DESC; 