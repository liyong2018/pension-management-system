-- 将菜单名称更新为中文
USE pension_management_system;

-- 更新顶级菜单名称
UPDATE menu_permission SET name = '首页' WHERE id = 1;
UPDATE menu_permission SET name = '机构管理' WHERE id = 2;
UPDATE menu_permission SET name = '人员档案' WHERE id = 3;
UPDATE menu_permission SET name = '智能设备' WHERE id = 4;
UPDATE menu_permission SET name = '服务记录' WHERE id = 5;
UPDATE menu_permission SET name = '志愿者管理' WHERE id = 6;
UPDATE menu_permission SET name = '系统管理' WHERE id = 7;

-- 更新智能设备子菜单名称
UPDATE menu_permission SET name = '设备管理' WHERE id = 11;
UPDATE menu_permission SET name = '告警管理' WHERE id = 12;

-- 更新系统管理子菜单名称
UPDATE menu_permission SET name = '用户管理' WHERE id = 21;
UPDATE menu_permission SET name = '角色管理' WHERE id = 22;
UPDATE menu_permission SET name = '权限管理' WHERE id = 23;
UPDATE menu_permission SET name = '菜单管理' WHERE id = 24;
UPDATE menu_permission SET name = '日志管理' WHERE id = 25;
UPDATE menu_permission SET name = '字典管理' WHERE id = 26;

-- 验证更新结果
SELECT '=== 更新后的顶级菜单 ===' as info;
SELECT id, name, type, route_path, sort_order, is_visible, status
FROM menu_permission 
WHERE parent_id IS NULL AND status = TRUE
ORDER BY sort_order;

SELECT '=== 更新后的子菜单 ===' as info;
SELECT p.name as parent_name, c.name as child_name, c.type, c.route_path
FROM menu_permission p
JOIN menu_permission c ON p.id = c.parent_id
WHERE p.status = TRUE AND c.status = TRUE
ORDER BY p.sort_order, c.sort_order; 