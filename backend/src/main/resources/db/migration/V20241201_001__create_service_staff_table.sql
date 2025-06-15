-- Create service staff table
CREATE TABLE service_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key ID',
    employee_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Employee ID (unique)',
    name VARCHAR(100) NOT NULL COMMENT 'Name',
    gender CHAR(1) NOT NULL COMMENT 'Gender (M-Male, F-Female)',
    birth_date DATE NOT NULL COMMENT 'Birth date',
    phone VARCHAR(20) NOT NULL COMMENT 'Phone number',
    email VARCHAR(100) COMMENT 'Email',
    id_card VARCHAR(18) NOT NULL COMMENT 'ID card number',
    address VARCHAR(200) COMMENT 'Address',
    position VARCHAR(100) NOT NULL COMMENT 'Position',
    department VARCHAR(100) COMMENT 'Department',
    work_type VARCHAR(20) NOT NULL COMMENT 'Work type (FULL_TIME, PART_TIME, CONTRACT)',
    hire_date DATE NOT NULL COMMENT 'Hire date',
    status VARCHAR(20) NOT NULL COMMENT 'Status (ACTIVE, INACTIVE, SUSPENDED)',
    base_salary DECIMAL(12,2) COMMENT 'Base salary',
    work_shift VARCHAR(20) COMMENT 'Work shift (DAY, NIGHT, ROTATING)',
    organization_id BIGINT NOT NULL COMMENT 'Organization ID',
    education VARCHAR(50) COMMENT 'Education level',
    skills TEXT COMMENT 'Skills (JSON format)',
    certifications TEXT COMMENT 'Certifications (JSON format)',
    emergency_contact_name VARCHAR(100) COMMENT 'Emergency contact name',
    emergency_contact_phone VARCHAR(20) COMMENT 'Emergency contact phone',
    emergency_contact_relationship VARCHAR(50) COMMENT 'Emergency contact relationship',
    health_status VARCHAR(200) COMMENT 'Health status',
    last_physical_exam_date DATE COMMENT 'Last physical exam date',
    next_physical_exam_date DATE COMMENT 'Next physical exam date',
    last_training_date DATE COMMENT 'Last training date',
    training_records TEXT COMMENT 'Training records (JSON format)',
    next_training_date DATE COMMENT 'Next training date',
    performance_score DECIMAL(5,2) COMMENT 'Performance score',
    last_evaluation_date DATE COMMENT 'Last evaluation date',
    next_evaluation_date DATE COMMENT 'Next evaluation date',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Created by user ID',
    updated_by BIGINT COMMENT 'Updated by user ID',
    remarks VARCHAR(500) COMMENT 'Remarks',
    avatar_url VARCHAR(200) COMMENT 'Avatar URL',
    work_status_stats TEXT COMMENT 'Work status statistics (JSON format)',
    service_stats TEXT COMMENT 'Service statistics (JSON format)',
    schedule_info TEXT COMMENT 'Schedule information (JSON format)',
    
    INDEX idx_employee_id (employee_id),
    INDEX idx_name (name),
    INDEX idx_department (department),
    INDEX idx_position (position),
    INDEX idx_status (status),
    INDEX idx_organization_id (organization_id),
    INDEX idx_hire_date (hire_date),
    INDEX idx_work_type (work_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Service staff table';

-- Insert test data
INSERT INTO service_staff (
    employee_id, name, gender, birth_date, phone, email, id_card, address,
    position, department, work_type, hire_date, status, base_salary, work_shift,
    organization_id, education, skills, certifications,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relationship,
    health_status, last_physical_exam_date, next_physical_exam_date,
    last_training_date, training_records, next_training_date,
    performance_score, last_evaluation_date, next_evaluation_date,
    created_by, updated_by, remarks, avatar_url,
    work_status_stats, service_stats, schedule_info
) VALUES 
-- 护理人员
('EMP001', '张小红', 'F', '1985-03-15', '13812345678', 'zhang.xiaohong@pension.com', '320102198503151234', '南京市玄武区中山路123号',
 '护理师', '护理部', 'FULL_TIME', '2020-01-15', 'ACTIVE', 6500.00, 'DAY',
 1, '护理专业大专', '{"skills": ["基础护理", "老年护理", "康复护理", "心理护理"]}', '{"certificates": ["护士执业证", "养老护理员证"]}',
 '张大明', '13987654321', '丈夫',
 '健康', '2024-01-15', '2025-01-15',
 '2024-03-01', '{"trainings": [{"date": "2024-03-01", "content": "老年痴呆护理培训", "hours": 8}]}', '2024-09-01',
 88.50, '2024-01-01', '2024-07-01',
 1, 1, '工作认真负责，深受老人喜爱', '/uploads/avatars/zhang_xiaohong.jpg',
 '{"total_work_days": 120, "attendance_rate": 98.5}', '{"service_count": 25, "satisfaction_rate": 95.2}', '{"current_shift": "白班", "next_shift_date": "2024-06-16"}')
,

-- 医生
('EMP002', '李医生', 'M', '1978-08-22', '13698765432', 'li.doctor@pension.com', '320103197808221567', '南京市秦淮区建康路456号',
 '主治医师', '医务科', 'FULL_TIME', '2018-05-20', 'ACTIVE', 12000.00, 'DAY',
 1, '临床医学硕士', '{"skills": ["内科诊疗", "老年病诊治", "慢性病管理", "急救处理"]}', '{"certificates": ["医师执业证", "主治医师证", "老年医学专科证"]}',
 '李夫人', '13876543210', '妻子',
 '健康', '2024-02-01', '2025-02-01',
 '2024-04-15', '{"trainings": [{"date": "2024-04-15", "content": "老年急症处理培训", "hours": 12}]}', '2024-10-15',
 92.00, '2024-02-01', '2024-08-01',
 1, 1, '医术精湛，经验丰富', '/uploads/avatars/li_doctor.jpg',
 '{"total_work_days": 118, "attendance_rate": 99.2}', '{"service_count": 45, "satisfaction_rate": 97.8}', '{"current_shift": "白班", "next_shift_date": "2024-06-16"}')
,

-- 康复师
('EMP003', '王康复', 'M', '1990-12-10', '13765432109', 'wang.kangfu@pension.com', '320104199012101890', '南京市鼓楼区中央路789号',
 '康复治疗师', '康复科', 'FULL_TIME', '2021-03-10', 'ACTIVE', 7500.00, 'DAY',
 1, '康复治疗学本科', '{"skills": ["物理治疗", "作业治疗", "言语治疗", "运动康复"]}', '{"certificates": ["康复治疗师证", "物理治疗师证"]}',
 '王母亲', '13654321098', '母亲',
 '健康', '2024-01-20', '2025-01-20',
 '2024-05-01', '{"trainings": [{"date": "2024-05-01", "content": "老年康复新技术培训", "hours": 16}]}', '2024-11-01',
 85.75, '2024-01-15', '2024-07-15',
 1, 1, '专业技能强，康复效果好', '/uploads/avatars/wang_kangfu.jpg',
 '{"total_work_days": 115, "attendance_rate": 97.8}', '{"service_count": 30, "satisfaction_rate": 94.5}', '{"current_shift": "白班", "next_shift_date": "2024-06-16"}')
,

-- 营养师
('EMP004', '陈营养', 'F', '1988-06-05', '13543210987', 'chen.yingyang@pension.com', '320105198806052345', '南京市建邺区江东中路321号',
 '营养师', '膳食科', 'FULL_TIME', '2019-09-01', 'ACTIVE', 6000.00, 'DAY',
 1, '营养学本科', '{"skills": ["营养评估", "膳食搭配", "特殊饮食", "营养咨询"]}', '{"certificates": ["公共营养师证", "临床营养师证"]}',
 '陈先生', '13432109876', '丈夫',
 '健康', '2024-03-01', '2025-03-01',
 '2024-02-20', '{"trainings": [{"date": "2024-02-20", "content": "老年营养管理培训", "hours": 10}]}', '2024-08-20',
 90.25, '2024-01-10', '2024-07-10',
 1, 1, '营养搭配合理，深受好评', '/uploads/avatars/chen_yingyang.jpg',
 '{"total_work_days": 122, "attendance_rate": 98.8}', '{"service_count": 35, "satisfaction_rate": 96.1}', '{"current_shift": "白班", "next_shift_date": "2024-06-16"}')
,

-- 夜班护理员
('EMP005', '刘夜班', 'F', '1992-11-18', '13321098765', 'liu.yeban@pension.com', '320106199211183456', '南京市雨花台区软件大道654号',
 '护理员', '护理部', 'FULL_TIME', '2022-06-15', 'ACTIVE', 5500.00, 'NIGHT',
 1, '护理专业中专', '{"skills": ["基础护理", "夜间护理", "应急处理", "老人陪护"]}', '{"certificates": ["养老护理员证"]}',
 '刘父亲', '13210987654', '父亲',
 '健康', '2024-02-15', '2025-02-15',
 '2024-04-01', '{"trainings": [{"date": "2024-04-01", "content": "夜间护理安全培训", "hours": 6}]}', '2024-10-01',
 87.00, '2024-02-01', '2024-08-01',
 1, 1, '夜班工作负责，应急能力强', '/uploads/avatars/liu_yeban.jpg',
 '{"total_work_days": 110, "attendance_rate": 96.5}', '{"service_count": 20, "satisfaction_rate": 93.8}', '{"current_shift": "夜班", "next_shift_date": "2024-06-16"}')
,

-- 兼职清洁员
('EMP006', '赵清洁', 'F', '1975-04-30', '13109876543', 'zhao.qingjie@pension.com', '320107197504304567', '南京市栖霞区仙林大道987号',
 '清洁员', '后勤部', 'PART_TIME', '2023-01-20', 'ACTIVE', 3000.00, 'DAY',
 1, '初中', '{"skills": ["环境清洁", "消毒杀菌", "垃圾分类"]}', '{"certificates": []}',
 '赵女儿', '13098765432', '女儿',
 '健康', '2024-01-10', '2025-01-10',
 '2024-03-15', '{"trainings": [{"date": "2024-03-15", "content": "清洁消毒标准培训", "hours": 4}]}', '2024-09-15',
 82.50, '2024-01-05', '2024-07-05',
 1, 1, '工作细致，环境维护好', '/uploads/avatars/zhao_qingjie.jpg',
 '{"total_work_days": 95, "attendance_rate": 95.0}', '{"service_count": 15, "satisfaction_rate": 91.2}', '{"current_shift": "白班", "next_shift_date": "2024-06-16"}')
;