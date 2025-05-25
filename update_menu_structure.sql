-- 菜单结构调整脚本
-- 将菜单结构调整为用户期望的层级结构

USE pension_management_system;

-- 设置字符集
SET NAMES utf8mb4;

-- 1. 首先添加两个新的顶级目录菜单
INSERT INTO menu_permission (id, parent_id, name, type, permission_key, route_path, component_path, icon, sort_order, is_visible, status, remark, create_time, update_time) VALUES
(200, NULL, '智能设备', 'CATALOG', 'smart-device:catalog', NULL, NULL, 'Monitor', 4, 1, 1, '智能设备管理目录', NOW(), NOW()),
(300, NULL, '系统管理', 'CATALOG', 'system:catalog', NULL, NULL, 'Setting', 7, 1, 1, '系统管理目录', NOW(), NOW());

-- 2. 更新设备管理和告警管理的父级ID，使其成为智能设备的子菜单
UPDATE menu_permission SET parent_id = 200, sort_order = 1 WHERE id = 4; -- 设备管理
UPDATE menu_permission SET parent_id = 200, sort_order = 2 WHERE id = 5; -- 告警管理

-- 3. 更新系统相关菜单的父级ID，使其成为系统管理的子菜单
UPDATE menu_permission SET parent_id = 300, sort_order = 1 WHERE id = 8;  -- 用户管理
UPDATE menu_permission SET parent_id = 300, sort_order = 2 WHERE id = 9;  -- 角色管理
UPDATE menu_permission SET parent_id = 300, sort_order = 3 WHERE id = 10; -- 权限管理
UPDATE menu_permission SET parent_id = 300, sort_order = 4 WHERE id = 11; -- 菜单管理
UPDATE menu_permission SET parent_id = 300, sort_order = 5 WHERE id = 12; -- 日志管理
UPDATE menu_permission SET parent_id = 300, sort_order = 6 WHERE id = 13; -- 字典管理

-- 4. 更新顶级菜单的排序
UPDATE menu_permission SET sort_order = 1 WHERE id = 1;   -- 首页
UPDATE menu_permission SET sort_order = 2 WHERE id = 2;   -- 机构管理
UPDATE menu_permission SET sort_order = 3 WHERE id = 3;   -- 人员档案
UPDATE menu_permission SET sort_order = 4 WHERE id = 200; -- 智能设备
UPDATE menu_permission SET sort_order = 5 WHERE id = 6;   -- 服务记录
UPDATE menu_permission SET sort_order = 6 WHERE id = 7;   -- 志愿者管理
UPDATE menu_permission SET sort_order = 7 WHERE id = 300; -- 系统管理

-- 5. 更新设备管理和告警管理的名称（如果需要）
UPDATE menu_permission SET name = '设备管理' WHERE id = 4;
UPDATE menu_permission SET name = '告警管理' WHERE id = 5;

-- 验证更新结果
SELECT 
    id,
    parent_id,
    name,
    type,
    route_path,
    sort_order,
    is_visible,
    status
FROM menu_permission 
WHERE status = 1 AND is_visible = 1
ORDER BY COALESCE(parent_id, id), sort_order; 