package com.example.pension.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private DataSource dataSource;

    /**
     * 检查用户和角色数据
     */
    @GetMapping("/check-data")
    public ResponseEntity<Map<String, Object>> checkData() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 查询所有用户
            String userSql = "SELECT id, username, full_name FROM system_user";
            List<Map<String, Object>> users = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(userSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getLong("id"));
                        user.put("username", rs.getString("username"));
                        user.put("fullName", rs.getString("full_name"));
                        users.add(user);
                    }
                }
            }
            result.put("users", users);
            
            // 查询所有角色
            String roleSql = "SELECT id, role_name, role_key FROM role";
            List<Map<String, Object>> roles = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(roleSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> role = new HashMap<>();
                        role.put("id", rs.getLong("id"));
                        role.put("roleName", rs.getString("role_name"));
                        role.put("roleKey", rs.getString("role_key"));
                        roles.add(role);
                    }
                }
            }
            result.put("roles", roles);
            
            // 查询现有的用户角色关联
            String userRoleSql = "SELECT user_id, role_id FROM user_role";
            List<Map<String, Object>> userRoles = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(userRoleSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> userRole = new HashMap<>();
                        userRole.put("userId", rs.getLong("user_id"));
                        userRole.put("roleId", rs.getLong("role_id"));
                        userRoles.add(userRole);
                    }
                }
            }
            result.put("userRoles", userRoles);
            
            result.put("status", "success");
            
        } catch (SQLException e) {
            log.error("检查数据失败", e);
            result.put("status", "error");
            result.put("message", "检查失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试角色分配
     */
    @PostMapping("/test-role-assign")
    public ResponseEntity<Map<String, Object>> testRoleAssign(
            @RequestParam Long userId, 
            @RequestParam Long roleId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 检查用户是否存在
            String checkUserSql = "SELECT COUNT(*) FROM system_user WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkUserSql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        result.put("status", "error");
                        result.put("message", "用户不存在，ID: " + userId);
                        return ResponseEntity.badRequest().body(result);
                    }
                }
            }
            
            // 检查角色是否存在
            String checkRoleSql = "SELECT COUNT(*) FROM role WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkRoleSql)) {
                stmt.setLong(1, roleId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        result.put("status", "error");
                        result.put("message", "角色不存在，ID: " + roleId);
                        return ResponseEntity.badRequest().body(result);
                    }
                }
            }
            
            // 检查是否已经存在该关联
            String checkExistsSql = "SELECT COUNT(*) FROM user_role WHERE user_id = ? AND role_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkExistsSql)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, roleId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("status", "warning");
                        result.put("message", "用户已分配该角色");
                        return ResponseEntity.ok(result);
                    }
                }
            }
            
            // 尝试插入用户角色关联
            String insertSql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, roleId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    result.put("status", "success");
                    result.put("message", "角色分配成功");
                } else {
                    result.put("status", "error");
                    result.put("message", "角色分配失败，没有插入任何记录");
                }
            }
            
        } catch (SQLException e) {
            log.error("测试角色分配失败", e);
            result.put("status", "error");
            result.put("message", "测试失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 查找特定用户
     */
    @GetMapping("/find-user")
    public ResponseEntity<Map<String, Object>> findUser(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT id, username, full_name, is_active FROM system_user WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getLong("id"));
                        user.put("username", rs.getString("username"));
                        user.put("fullName", rs.getString("full_name"));
                        user.put("isActive", rs.getBoolean("is_active"));
                        result.put("user", user);
                        result.put("found", true);
                    } else {
                        result.put("found", false);
                        result.put("message", "用户不存在: " + username);
                    }
                }
            }
            
            result.put("status", "success");
            
        } catch (SQLException e) {
            log.error("查找用户失败", e);
            result.put("status", "error");
            result.put("message", "查找失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
} 