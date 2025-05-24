-- 最小化的数据库脚本，解决依赖关系问题
-- 数据库: pension_management_system

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除所有表
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS menu_permission;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS volunteer_service_assignment;
DROP TABLE IF EXISTS volunteer_service_project;
DROP TABLE IF EXISTS device_maintenance_record;
DROP TABLE IF EXISTS device_data_record;
DROP TABLE IF EXISTS device_alarm_record;
DROP TABLE IF EXISTS health_monitoring_data;
DROP TABLE IF EXISTS smart_device;
DROP TABLE IF EXISTS service_record;
DROP TABLE IF EXISTS elderly_family_member;
DROP TABLE IF EXISTS elderly_profile;
DROP TABLE IF EXISTS system_user;
DROP TABLE IF EXISTS volunteer;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS organization;
DROP TABLE IF EXISTS homepage_statistics;

-- 创建表（按正确的依赖顺序）

-- 1. 机构表（基础表）
CREATE TABLE organization (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  short_name VARCHAR(100),
  type VARCHAR(50),
  address VARCHAR(255),
  phone VARCHAR(20),
  email VARCHAR(100),
  website VARCHAR(255),
  establishment_date DATE,
  license_number VARCHAR(100),
  business_scope TEXT,
  bed_count INT,
  actual_service_count INT,
  charging_standard TEXT,
  area VARCHAR(100),
  director_name VARCHAR(50),
  director_contact VARCHAR(100),
  employee_count INT,
  professional_nurse_count INT,
  fire_license VARCHAR(255),
  sanitary_license VARCHAR(255),
  medical_license VARCHAR(255),
  other_qualifications TEXT,
  description TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 角色表（基础表）
CREATE TABLE role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL UNIQUE,
  role_key VARCHAR(50) NOT NULL UNIQUE,
  description TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 系统用户表
CREATE TABLE system_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  phone VARCHAR(20),
  organization_id BIGINT,
  is_admin BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  last_login_time DATETIME,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_user_org (organization_id),
  CONSTRAINT fk_user_org FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 老人档案表
CREATE TABLE elderly_profile (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  gender VARCHAR(10),
  birth_date DATE,
  id_card_number VARCHAR(18) UNIQUE,
  phone VARCHAR(20),
  photo_url VARCHAR(255),
  address_detail VARCHAR(255),
  community VARCHAR(100),
  pension_type VARCHAR(50),
  medical_history TEXT,
  allergy_history TEXT,
  physical_exam_report TEXT,
  current_health_status TEXT,
  care_level VARCHAR(50),
  ability_assessment VARCHAR(50),
  living_habits TEXT,
  hobbies TEXT,
  religious_belief VARCHAR(100),
  remarks TEXT,
  organization_id BIGINT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_elderly_org (organization_id),
  CONSTRAINT fk_elderly_org FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 用户角色关联表
CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES system_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 菜单权限表（暂时不添加自引用外键）
CREATE TABLE menu_permission (
  id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT DEFAULT NULL,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL,
  permission_key VARCHAR(100) UNIQUE,
  route_path VARCHAR(255),
  component_path VARCHAR(255),
  icon VARCHAR(100),
  sort_order INT DEFAULT 0,
  is_visible BOOLEAN DEFAULT TRUE,
  status BOOLEAN DEFAULT TRUE,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 角色权限关联表
CREATE TABLE role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
  CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES menu_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 服务记录表
CREATE TABLE service_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  elderly_id BIGINT NOT NULL,
  service_content TEXT NOT NULL,
  service_time DATETIME NOT NULL,
  service_address VARCHAR(255),
  service_provider_type VARCHAR(50),
  service_provider_id BIGINT,
  service_provider_name VARCHAR(100),
  work_order_price DECIMAL(10, 2),
  status VARCHAR(50),
  evaluation_score INT,
  evaluation_comment TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_service_elderly FOREIGN KEY (elderly_id) REFERENCES elderly_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 智能设备表
CREATE TABLE smart_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(100) NOT NULL UNIQUE,
    device_name VARCHAR(200) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    device_brand VARCHAR(100),
    device_model VARCHAR(100),
    device_status VARCHAR(20) NOT NULL DEFAULT '在线',
    purchase_date DATE,
    installation_location VARCHAR(500),
    elderly_id BIGINT,
    organization_id BIGINT,
    ip_address VARCHAR(50),
    mac_address VARCHAR(50),
    communication_protocol VARCHAR(50),
    data_collection_frequency INT DEFAULT 60,
    alarm_threshold TEXT,
    battery_threshold INT DEFAULT 20,
    real_time_data TEXT,
    battery_level INT,
    signal_strength INT,
    last_communication_time DATETIME,
    data_upload_status VARCHAR(20) DEFAULT '正常',
    warranty_expiry_date DATE,
    maintenance_cycle INT,
    next_maintenance_date DATE,
    failure_count INT DEFAULT 0,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_device_code (device_code),
    INDEX idx_device_type (device_type),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_organization_id (organization_id),
    CONSTRAINT fk_device_elderly FOREIGN KEY (elderly_id) REFERENCES elderly_profile(id) ON DELETE SET NULL,
    CONSTRAINT fk_device_org FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 添加自引用外键
ALTER TABLE menu_permission ADD CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES menu_permission(id) ON DELETE CASCADE;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入基础数据

-- 插入角色
INSERT INTO role (role_name, role_key, description) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('机构管理员', 'ORG_ADMIN', '管理本机构相关事务'),
('普通用户', 'USER', '基本查看权限');

-- 插入机构
INSERT INTO organization (name, short_name, type, address, phone, email, establishment_date, license_number, bed_count, actual_service_count, director_name, director_contact, employee_count, professional_nurse_count) VALUES 
('阳光老年公寓', '阳光公寓', '机构养老单位（养老院）', '北京市朝阳区阳光路123号', '010-12345678', 'yangguang@example.com', '2015-01-15', 'YL2015001', 200, 180, '张明', '13901234567', 50, 20),
('和谐养老院', '和谐院', '机构养老单位（养老院）', '上海市浦东新区和谐路456号', '021-87654321', 'hexie@example.com', '2016-03-20', 'YL2016002', 150, 130, '李华', '13812345678', 40, 15),
('乐龄社区日照中心', '乐龄中心', '社区养老单位（日照）', '广州市天河区乐龄路789号', '020-98765432', 'leling@example.com', '2017-05-10', 'YL2017003', 0, 50, '王芳', '13923456789', 15, 5);

-- 插入系统用户
INSERT INTO system_user (username, password_hash, full_name, email, is_admin, is_active) VALUES 
('admin', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '系统管理员', 'admin@example.com', true, true);

-- 插入用户角色关联
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

-- 插入老人档案
INSERT INTO elderly_profile (name, gender, birth_date, id_card_number, phone, address_detail, community, pension_type, organization_id) VALUES 
('王建国', '男', '1938-03-15', '110101193803152011', '13601234567', '北京市朝阳区朝阳公园南路123号', '朝阳公园社区', '居家养老', 1),
('李秀梅', '女', '1941-07-22', '110101194107228524', '13701234568', '北京市海淀区中关村大街45号', '中关村社区', '社区养老（日照）', 2),
('张志强', '男', '1935-12-08', '110101193512083719', '13801234569', '北京市西城区西单北大街78号', '西单社区', '机构养老（养老院）', 3);

-- 插入服务记录
INSERT INTO service_record (elderly_id, service_content, service_time, service_address, service_provider_type, service_provider_name, work_order_price, status) VALUES
(1, '日常生活护理服务', '2024-01-15 10:00:00', '北京市朝阳区', '机构员工', '张护士', 50.00, '已完成'),
(2, '健康检查服务', '2024-01-16 14:30:00', '北京市海淀区', '机构员工', '李医生', 80.00, '进行中'),
(3, '医疗咨询服务', '2024-01-15 10:00:00', '北京朝阳养老院', '志愿者', '王志愿者', 0.00, '待处理');

-- 插入智能设备
INSERT INTO smart_device (device_code, device_name, device_type, device_brand, device_model, device_status, purchase_date, installation_location, elderly_id, organization_id, created_by) VALUES
('DEV001', '智能手环Pro', '健康监测设备', '小米', 'Mi Band 7', '在线', '2023-01-15', '张伟 卧室', 1, 1, 'admin'),
('DEV002', '血压监测仪', '健康监测设备', '欧姆龙', 'HEM-7156', '在线', '2023-02-20', '李淑华 客厅', 2, 1, 'admin');

SELECT 'Database initialization completed successfully!' as message; 