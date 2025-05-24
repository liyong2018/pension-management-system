package com.example.pension.controller;

import com.example.pension.dto.RoleDTO;
import com.example.pension.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<RoleDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<RoleDTO> result = roleService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有角色（不分页）
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> result = roleService.getAllRoles();
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索角色
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<RoleDTO>> search(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<RoleDTO> result = roleService.searchByConditions(roleName, roleKey, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        RoleDTO result = roleService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新角色
     */
    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO roleDTO) {
        RoleDTO result = roleService.create(roleDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(
            @PathVariable Long id,
            @RequestBody RoleDTO roleDTO) {
        RoleDTO result = roleService.update(id, roleDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        roleService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 分配权限给角色
     */
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<Void> removePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.removePermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新角色权限
     */
    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<Void> updateRolePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据用户ID获取角色列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleDTO>> getRolesByUserId(@PathVariable Long userId) {
        List<RoleDTO> result = roleService.getRolesByUserId(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查角色键是否存在
     */
    @GetMapping("/check-role-key")
    public ResponseEntity<Boolean> checkRoleKey(
            @RequestParam String roleKey,
            @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = roleService.existsByRoleKeyExcludeId(roleKey, excludeId);
        } else {
            exists = roleService.existsByRoleKey(roleKey);
        }
        return ResponseEntity.ok(exists);
    }
} 