-- 恢复完整菜单数据脚本（英文版）
USE pension_management_system;

-- 清空现有菜单权限数据
DELETE FROM role_permission;
DELETE FROM menu_permission;

-- 重置自增ID
ALTER TABLE menu_permission AUTO_INCREMENT = 1;

-- 插入顶级菜单
INSERT INTO menu_permission (id, parent_id, name, type, permission_key, route_path, component_path, icon, sort_order, is_visible, status) VALUES
-- 1. 首页
(1, NULL, 'Home', 'MENU', 'home:view', '/', 'HomeView', 'House', 1, TRUE, TRUE),

-- 2. 机构管理
(2, NULL, 'Organization Management', 'MENU', 'organization:view', '/organization-management', 'views/organization/OrganizationIndex', 'OfficeBuilding', 2, TRUE, TRUE),

-- 3. 人员档案
(3, NULL, 'Elderly Profiles', 'MENU', 'elderly:view', '/elderly-profiles', 'views/elderly/ElderlyProfileList', 'User', 3, TRUE, TRUE),

-- 4. 智能设备（目录）
(4, NULL, 'Smart Devices', 'CATALOG', 'smart-device:view', '/smart-device', NULL, 'Monitor', 4, TRUE, TRUE),

-- 5. 服务记录
(5, NULL, 'Service Records', 'MENU', 'service:view', '/service-records', 'views/service-record/ServiceRecordList', 'Document', 5, TRUE, TRUE),

-- 6. 志愿者管理
(6, NULL, 'Volunteer Management', 'MENU', 'volunteer:view', '/volunteers', 'views/volunteer/VolunteerListSimple', 'Avatar', 6, TRUE, TRUE),

-- 7. 系统管理（目录）
(7, NULL, 'System Management', 'CATALOG', 'system:view', '/system', NULL, 'Setting', 7, TRUE, TRUE);

-- 插入智能设备子菜单
INSERT INTO menu_permission (id, parent_id, name, type, permission_key, route_path, component_path, icon, sort_order, is_visible, status) VALUES
-- 智能设备子菜单
(11, 4, 'Device Management', 'MENU', 'smart-device:manage', '/smart-devices', 'views/smart-device/SmartDeviceList', 'Monitor', 1, TRUE, TRUE),
(12, 4, 'Alarm Management', 'MENU', 'smart-device:alarm', '/device-alarms', 'views/smart-device/DeviceAlarmList', 'Warning', 2, TRUE, TRUE);

-- 插入系统管理子菜单
INSERT INTO menu_permission (id, parent_id, name, type, permission_key, route_path, component_path, icon, sort_order, is_visible, status) VALUES
-- 系统管理子菜单
(21, 7, 'User Management', 'MENU', 'system:user:manage', '/system/users', 'views/system/SystemUserList', 'User', 1, TRUE, TRUE),
(22, 7, 'Role Management', 'MENU', 'system:role:manage', '/system/roles', 'views/system/RoleList', 'Avatar', 2, TRUE, TRUE),
(23, 7, 'Permission Management', 'MENU', 'system:permission:manage', '/system/permissions', 'views/system/PermissionList', 'Key', 3, TRUE, TRUE),
(24, 7, 'Menu Management', 'MENU', 'system:menu:manage', '/system/menus', 'views/system/MenuList', 'Menu', 4, TRUE, TRUE),
(25, 7, 'Log Management', 'MENU', 'system:log:manage', '/system/logs', 'views/system/LogList', 'Document', 5, TRUE, TRUE),
(26, 7, 'Dictionary Management', 'MENU', 'system:dict:manage', '/system/dictionaries', 'views/system/DictionaryManagementSimple', 'Collection', 6, TRUE, TRUE);

-- 为超级管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM menu_permission WHERE status = TRUE;

-- 验证插入结果
SELECT '=== Menu Data Restored ===' as status;
SELECT 
    COUNT(*) as total_permissions,
    COUNT(CASE WHEN type = 'CATALOG' THEN 1 END) as catalogs,
    COUNT(CASE WHEN type = 'MENU' THEN 1 END) as menus,
    COUNT(CASE WHEN parent_id IS NULL THEN 1 END) as top_level_menus
FROM menu_permission;

-- 显示顶级菜单
SELECT '=== Top Level Menus ===' as info;
SELECT id, name, type, route_path, sort_order, is_visible, status
FROM menu_permission 
WHERE parent_id IS NULL AND status = TRUE
ORDER BY sort_order; 