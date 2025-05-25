package com.example.pension.controller;

import com.example.pension.dto.SystemUserDTO;
import com.example.pension.dto.DirectorDTO;
import com.example.pension.service.SystemUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system-users")
@CrossOrigin(origins = "*")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<SystemUserDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<SystemUserDTO> result = systemUserService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<SystemUserDTO>> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Boolean isAdmin,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<SystemUserDTO> result = systemUserService.searchByMultipleConditions(
                username, fullName, email, organizationId, isAdmin, isActive, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemUserDTO> getById(@PathVariable Long id) {
        SystemUserDTO result = systemUserService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<SystemUserDTO> getByUsername(@PathVariable String username) {
        SystemUserDTO result = systemUserService.getByUsername(username);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<SystemUserDTO> create(@RequestBody SystemUserDTO systemUserDTO) {
        SystemUserDTO result = systemUserService.create(systemUserDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<SystemUserDTO> update(
            @PathVariable Long id,
            @RequestBody SystemUserDTO systemUserDTO) {
        SystemUserDTO result = systemUserService.update(id, systemUserDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        systemUserService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        systemUserService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新用户密码
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        systemUserService.updatePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * 分配角色给用户
     */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        systemUserService.assignRoles(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<Void> removeRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        systemUserService.removeRoles(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<Void> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> roleIds = request.get("roleIds");
        systemUserService.updateUserRoles(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据角色ID获取用户列表
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<SystemUserDTO>> getUsersByRoleId(@PathVariable Long roleId) {
        List<SystemUserDTO> result = systemUserService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        Map<String, Long> result = systemUserService.getUserStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 统计活跃用户数
     */
    @GetMapping("/statistics/active")
    public ResponseEntity<Long> countActiveUsers() {
        Long result = systemUserService.countActiveUsers();
        return ResponseEntity.ok(result);
    }

    /**
     * 统计管理员用户数
     */
    @GetMapping("/statistics/admin")
    public ResponseEntity<Long> countAdminUsers() {
        Long result = systemUserService.countAdminUsers();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据机构ID统计用户数
     */
    @GetMapping("/statistics/organization/{organizationId}")
    public ResponseEntity<Long> countByOrganizationId(@PathVariable Long organizationId) {
        Long result = systemUserService.countByOrganizationId(organizationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id) {
        systemUserService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 切换用户状态（启用/禁用）
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id) {
        systemUserService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Map<String, Object>>> getUserRoles(@PathVariable Long id) {
        List<Map<String, Object>> result = systemUserService.getUserRoles(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有机构负责人列表
     * @return 机构负责人列表
     */
    @GetMapping("/directors")
    public ResponseEntity<List<DirectorDTO>> getAllDirectors() {
        try {
            List<DirectorDTO> directors = systemUserService.getAllDirectors();
            return ResponseEntity.ok(directors);
        } catch (Exception e) {
            log.error("获取机构负责人列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据机构ID获取机构负责人列表
     * @param organizationId 机构ID（可选）
     * @return 机构负责人列表
     */
    @GetMapping("/directors/by-organization")
    public ResponseEntity<List<DirectorDTO>> getDirectorsByOrganization(
            @RequestParam(required = false) Long organizationId) {
        try {
            List<DirectorDTO> directors = systemUserService.getDirectorsByOrganization(organizationId);
            return ResponseEntity.ok(directors);
        } catch (Exception e) {
            log.error("根据机构获取负责人列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 