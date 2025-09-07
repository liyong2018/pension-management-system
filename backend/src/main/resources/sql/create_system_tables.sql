-- Create system tables for pension management system

-- Create organization table first (referenced by system_user)
CREATE TABLE IF NOT EXISTS `organization` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL COMMENT '机构名称',
    `code` VARCHAR(100) UNIQUE COMMENT '机构代码',
    `type` VARCHAR(50) COMMENT '机构类型',
    `address` TEXT COMMENT '机构地址',
    `contact_person` VARCHAR(100) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_email` VARCHAR(100) COMMENT '联系邮箱',
    `description` TEXT COMMENT '机构描述',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_organization_code` (`code`),
    INDEX `idx_organization_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机构表';

-- Create system_user table
CREATE TABLE IF NOT EXISTS `system_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `full_name` VARCHAR(100) NOT NULL COMMENT '姓名',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `organization_id` BIGINT COMMENT '所属机构ID',
    `is_admin` BOOLEAN DEFAULT FALSE COMMENT '是否管理员',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `last_login_time` TIMESTAMP NULL COMMENT '最后登录时间',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_system_user_username` (`username`),
    INDEX `idx_system_user_email` (`email`),
    INDEX `idx_system_user_organization` (`organization_id`),
    FOREIGN KEY (`organization_id`) REFERENCES `organization`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- Create role table
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    `description` TEXT COMMENT '角色描述',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- Create menu_permission table
CREATE TABLE IF NOT EXISTS `menu_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `parent_id` BIGINT COMMENT '父级权限ID',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `type` ENUM('CATALOG', 'MENU', 'BUTTON') NOT NULL COMMENT '权限类型',
    `permission_key` VARCHAR(200) NOT NULL COMMENT '权限标识',
    `route_path` VARCHAR(200) COMMENT '路由路径',
    `component_path` VARCHAR(200) COMMENT '组件路径',
    `icon` VARCHAR(100) COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_visible` BOOLEAN DEFAULT TRUE COMMENT '是否可见',
    `status` BOOLEAN DEFAULT TRUE COMMENT '状态',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_menu_permission_parent` (`parent_id`),
    INDEX `idx_menu_permission_key` (`permission_key`),
    FOREIGN KEY (`parent_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- Create user_role table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `system_user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- Create role_permission table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `menu_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- Insert default organization
INSERT IGNORE INTO `organization` (`id`, `name`, `code`, `type`, `description`) VALUES
(1, '系统默认机构', 'DEFAULT_ORG', 'SYSTEM', '系统默认机构，用于初始化');

-- Insert default roles
INSERT IGNORE INTO `role` (`id`, `name`, `code`, `description`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
(2, '机构管理员', 'ORG_ADMIN', '机构管理员，管理本机构相关业务'),
(3, '普通用户', 'USER', '普通用户，基本查看权限');

-- Insert default admin user (password: admin123)
INSERT IGNORE INTO `system_user` (`id`, `username`, `password_hash`, `full_name`, `email`, `organization_id`, `is_admin`, `is_active`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfcqkunzgTB2l2EmAZPMROK2', '系统管理员', 'admin@pension.com', 1, TRUE, TRUE);

-- Assign super admin role to default admin user
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

SELECT 'System tables created successfully!' as status;