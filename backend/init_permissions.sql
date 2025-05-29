-- 权限数据快速初始化脚本
-- 如果权限接口返回500错误，请在数据库中执行此脚本

-- 清空现有权限数据
DELETE FROM `role_permission`;
DELETE FROM `menu_permission`;

-- 重置自增ID
ALTER TABLE `menu_permission` AUTO_INCREMENT = 1;

-- 插入顶级菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 1. 首页
(1, NULL, '首页', 'MENU', 'home:view', '/', 'HomeView', 'House', 1, TRUE, TRUE),

-- 2. 机构管理
(2, NULL, '机构管理', 'MENU', 'organization:view', '/organization-management', 'views/organization/OrganizationIndex', 'OfficeBuilding', 2, TRUE, TRUE),

-- 3. 人员档案
(3, NULL, '人员档案', 'MENU', 'elderly:view', '/elderly-profiles', 'views/elderly/ElderlyProfileList', 'User', 3, TRUE, TRUE),

-- 4. 智能设备（目录）
(4, NULL, '智能设备', 'CATALOG', 'smart-device:view', '/smart-device', NULL, 'Monitor', 4, TRUE, TRUE),

-- 5. 服务记录
(5, NULL, '服务记录', 'MENU', 'service:view', '/service-records', 'views/service-record/ServiceRecordList', 'Document', 5, TRUE, TRUE),

-- 6. 志愿者管理
(6, NULL, '志愿者管理', 'MENU', 'volunteer:view', '/volunteers', 'views/volunteer/VolunteerListSimple', 'Avatar', 6, TRUE, TRUE),

-- 7. 系统管理（目录）
(7, NULL, '系统管理', 'CATALOG', 'system:view', '/system', NULL, 'Setting', 7, TRUE, TRUE);

-- 插入智能设备子菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 智能设备子菜单
(11, 4, '设备管理', 'MENU', 'smart-device:manage', '/smart-devices', 'views/smart-device/SmartDeviceList', 'Monitor', 1, TRUE, TRUE),
(12, 4, '告警管理', 'MENU', 'smart-device:alarm', '/device-alarms', 'views/smart-device/DeviceAlarmList', 'Warning', 2, TRUE, TRUE);

-- 插入系统管理子菜单
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 系统管理子菜单
(21, 7, '用户管理', 'MENU', 'system:user:manage', '/system/users', 'views/system/SystemUserList', 'User', 1, TRUE, TRUE),
(22, 7, '角色管理', 'MENU', 'system:role:manage', '/system/roles', 'views/system/RoleList', 'Avatar', 2, TRUE, TRUE),
(23, 7, '权限管理', 'MENU', 'system:permission:manage', '/system/permissions', 'views/system/PermissionList', 'Key', 3, TRUE, TRUE),
(24, 7, '菜单管理', 'MENU', 'system:menu:manage', '/system/menus', 'views/system/MenuList', 'Menu', 4, TRUE, TRUE),
(25, 7, '日志管理', 'MENU', 'system:log:manage', '/system/logs', 'views/system/LogList', 'Document', 5, TRUE, TRUE),
(26, 7, '字典管理', 'MENU', 'system:dict:manage', '/system/dictionaries', 'views/system/DictionaryManagementSimple', 'Collection', 6, TRUE, TRUE);

-- 插入按钮权限（针对各个功能模块）
INSERT INTO `menu_permission` (`id`, `parent_id`, `name`, `type`, `permission_key`, `route_path`, `component_path`, `icon`, `sort_order`, `is_visible`, `status`) VALUES
-- 机构管理按钮权限
(101, 2, '新增机构', 'BUTTON', 'organization:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(102, 2, '编辑机构', 'BUTTON', 'organization:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(103, 2, '删除机构', 'BUTTON', 'organization:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(104, 2, '导出机构', 'BUTTON', 'organization:export', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 人员档案按钮权限
(111, 3, '新增档案', 'BUTTON', 'elderly:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(112, 3, '编辑档案', 'BUTTON', 'elderly:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(113, 3, '删除档案', 'BUTTON', 'elderly:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(114, 3, '导出档案', 'BUTTON', 'elderly:export', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 智能设备按钮权限
(121, 11, '新增设备', 'BUTTON', 'smart-device:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(122, 11, '编辑设备', 'BUTTON', 'smart-device:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(123, 11, '删除设备', 'BUTTON', 'smart-device:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(124, 12, '处理告警', 'BUTTON', 'smart-device:alarm:handle', NULL, NULL, NULL, 1, FALSE, TRUE),

-- 服务记录按钮权限
(131, 5, '新增记录', 'BUTTON', 'service:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(132, 5, '编辑记录', 'BUTTON', 'service:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(133, 5, '删除记录', 'BUTTON', 'service:delete', NULL, NULL, NULL, 3, FALSE, TRUE),

-- 志愿者管理按钮权限
(141, 6, '新增志愿者', 'BUTTON', 'volunteer:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(142, 6, '编辑志愿者', 'BUTTON', 'volunteer:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(143, 6, '删除志愿者', 'BUTTON', 'volunteer:delete', NULL, NULL, NULL, 3, FALSE, TRUE),

-- 用户管理按钮权限
(151, 21, '新增用户', 'BUTTON', 'system:user:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(152, 21, '编辑用户', 'BUTTON', 'system:user:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(153, 21, '删除用户', 'BUTTON', 'system:user:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(154, 21, '重置密码', 'BUTTON', 'system:user:reset-password', NULL, NULL, NULL, 4, FALSE, TRUE),
(155, 21, '分配角色', 'BUTTON', 'system:user:assign-role', NULL, NULL, NULL, 5, FALSE, TRUE),

-- 角色管理按钮权限
(161, 22, '新增角色', 'BUTTON', 'system:role:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(162, 22, '编辑角色', 'BUTTON', 'system:role:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(163, 22, '删除角色', 'BUTTON', 'system:role:delete', NULL, NULL, NULL, 3, FALSE, TRUE),
(164, 22, '分配权限', 'BUTTON', 'system:role:assign-permission', NULL, NULL, NULL, 4, FALSE, TRUE),

-- 菜单管理按钮权限
(171, 24, '新增菜单', 'BUTTON', 'system:menu:add', NULL, NULL, NULL, 1, FALSE, TRUE),
(172, 24, '编辑菜单', 'BUTTON', 'system:menu:edit', NULL, NULL, NULL, 2, FALSE, TRUE),
(173, 24, '删除菜单', 'BUTTON', 'system:menu:delete', NULL, NULL, NULL, 3, FALSE, TRUE);

-- 为超级管理员角色分配所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `menu_permission` WHERE `status` = TRUE;

-- 为机构管理员角色分配基本权限（排除系统管理）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `menu_permission` 
WHERE `status` = TRUE 
AND id NOT IN (7, 21, 22, 23, 24, 25, 26, 151, 152, 153, 154, 155, 161, 162, 163, 164, 171, 172, 173);

-- 为普通用户角色分配查看权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `menu_permission` 
WHERE `status` = TRUE 
AND `type` IN ('MENU', 'CATALOG')
AND id NOT IN (7, 21, 22, 23, 24, 25, 26);

-- 验证插入结果
SELECT 
    '=== 菜单权限数据初始化完成 ===' as status,
    (SELECT COUNT(*) FROM `menu_permission`) as total_permissions,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'CATALOG') as catalogs,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'MENU') as menus,
    (SELECT COUNT(*) FROM `menu_permission` WHERE `type` = 'BUTTON') as buttons,
    (SELECT COUNT(*) FROM `role_permission`) as role_permissions; 