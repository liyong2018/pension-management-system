-- 更新服务记录表中的英文数据为中文
USE pension_management_system;

-- 更新服务内容
UPDATE service_record 
SET service_content = CASE 
    WHEN service_content = 'Health check service' THEN '健康检查服务'
    WHEN service_content = 'Daily care service' THEN '日常生活护理服务'
    WHEN service_content = 'Medica consultation' THEN '医疗咨询服务'
    WHEN service_content = 'Medical consultation' THEN '医疗咨询服务'
    WHEN service_content = 'Rehabilitation training' THEN '康复训练服务'
    WHEN service_content = 'Psychological counseling' THEN '心理咨询服务'
    ELSE service_content
END;

-- 更新服务地址
UPDATE service_record 
SET service_address = CASE 
    WHEN service_address = 'Beijing Chaoyang District' THEN '北京市朝阳区'
    WHEN service_address = 'Beijing Haidian District' THEN '北京市海淀区'
    WHEN service_address = 'Beijing Chaoyang Nursing Home' THEN '北京朝阳养老院'
    WHEN service_address = 'Beijing Xicheng District' THEN '北京市西城区'
    ELSE service_address
END;

-- 更新服务提供者类型
UPDATE service_record 
SET service_provider_type = CASE 
    WHEN service_provider_type = 'Staff' THEN '机构员工'
    WHEN service_provider_type = 'Volunteer' THEN '志愿者'
    WHEN service_provider_type = 'Outsourced' THEN '外包服务'
    ELSE service_provider_type
END;

-- 更新服务提供者姓名
UPDATE service_record 
SET service_provider_name = CASE 
    WHEN service_provider_name = 'Zhang Nurse' THEN '张护士'
    WHEN service_provider_name = 'Li Doctor' THEN '李医生'
    WHEN service_provider_name = 'Wang Nurse' THEN '王护士'
    WHEN service_provider_name = 'Zhao Therapist' THEN '赵康复师'
    WHEN service_provider_name = 'Chen Psychologist' THEN '陈心理师'
    ELSE service_provider_name
END;

-- 查看更新结果
SELECT id, elderly_id, service_content, service_address, service_provider_type, service_provider_name, status 
FROM service_record 
ORDER BY id; 