-- 初始管理员用户 (密码: admin123)
INSERT INTO users (username, password, full_name, email, is_admin, is_active, created_at)
SELECT 'admin', '$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi', '系统管理员', 'admin@example.com', true, true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin'); 