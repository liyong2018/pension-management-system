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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/database-fix")
@CrossOrigin(origins = "*")
public class DatabaseFixController {

    @Autowired
    private DataSource dataSource;

    /**
     * 修复角色表缺少status字段的问题
     */
    @PostMapping("/fix-role-status")
    public ResponseEntity<Map<String, Object>> fixRoleStatusField() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 1. 检查status字段是否存在
            boolean statusFieldExists = checkColumnExists(connection, "role", "status");
            
            if (statusFieldExists) {
                result.put("status", "success");
                result.put("message", "role表的status字段已存在，无需修复");
                return ResponseEntity.ok(result);
            }
            
            // 2. 添加status字段
            String alterTableSql = "ALTER TABLE `role` ADD COLUMN `status` VARCHAR(1) DEFAULT '1' COMMENT '状态：1启用，0禁用'";
            try (PreparedStatement stmt = connection.prepareStatement(alterTableSql)) {
                stmt.executeUpdate();
                log.info("成功添加role表的status字段");
            }
            
            // 3. 更新现有数据
            String updateSql = "UPDATE `role` SET `status` = '1' WHERE `status` IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                int updatedRows = stmt.executeUpdate();
                log.info("更新了{}行数据的status字段", updatedRows);
            }
            
            result.put("status", "success");
            result.put("message", "role表status字段修复成功");
            
        } catch (SQLException e) {
            log.error("修复role表status字段失败", e);
            result.put("status", "error");
            result.put("message", "修复失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 检查数据库表字段是否存在
     */
    private boolean checkColumnExists(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            stmt.setString(2, columnName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * 检查数据库状态
     */
    @GetMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkDatabaseStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 检查role表的status字段
            boolean roleStatusExists = checkColumnExists(connection, "role", "status");
            result.put("roleStatusFieldExists", roleStatusExists);
            
            // 检查role表数据
            String countSql = "SELECT COUNT(*) as count FROM role";
            try (PreparedStatement stmt = connection.prepareStatement(countSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.put("roleCount", rs.getInt("count"));
                    }
                }
            }
            
            result.put("status", "success");
            result.put("message", "数据库状态检查完成");
            
        } catch (SQLException e) {
            log.error("检查数据库状态失败", e);
            result.put("status", "error");
            result.put("message", "检查失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
} 