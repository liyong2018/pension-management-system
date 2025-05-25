package com.example.pension.controller;

import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.dto.RoleDTO;
import com.example.pension.service.MenuPermissionService;
import com.example.pension.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class MenuPermissionController {

    @Autowired
    private MenuPermissionService menuPermissionService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取权限列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<MenuPermissionDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<MenuPermissionDTO> result = menuPermissionService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有权限（不分页）
     */
    @GetMapping("/all")
    public ResponseEntity<List<MenuPermissionDTO>> getAllPermissions() {
        List<MenuPermissionDTO> result = menuPermissionService.getAllPermissions();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取权限树形结构
     */
    @GetMapping("/tree")
    public ResponseEntity<List<MenuPermissionDTO>> getPermissionTree() {
        List<MenuPermissionDTO> result = menuPermissionService.getPermissionTree();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取子权限列表
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<MenuPermissionDTO>> getChildrenByParentId(@PathVariable Long parentId) {
        List<MenuPermissionDTO> result = menuPermissionService.getChildrenByParentId(parentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索权限
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<MenuPermissionDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String permissionKey,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<MenuPermissionDTO> result = menuPermissionService.searchByConditions(
                name, type, permissionKey, status, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个权限详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuPermissionDTO> getById(@PathVariable Long id) {
        MenuPermissionDTO result = menuPermissionService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新权限
     */
    @PostMapping
    public ResponseEntity<MenuPermissionDTO> create(@RequestBody MenuPermissionDTO menuPermissionDTO) {
        MenuPermissionDTO result = menuPermissionService.create(menuPermissionDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新权限信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuPermissionDTO> update(
            @PathVariable Long id,
            @RequestBody MenuPermissionDTO menuPermissionDTO) {
        MenuPermissionDTO result = menuPermissionService.update(id, menuPermissionDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuPermissionService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除权限
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        menuPermissionService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据角色ID获取权限列表
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<MenuPermissionDTO>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<MenuPermissionDTO> result = menuPermissionService.getPermissionsByRoleId(roleId);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据用户ID获取权限列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MenuPermissionDTO>> getPermissionsByUserId(@PathVariable Long userId) {
        List<MenuPermissionDTO> result = menuPermissionService.getPermissionsByUserId(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取菜单权限树形结构
     */
    @GetMapping("/menu-tree")
    public ResponseEntity<List<MenuPermissionDTO>> getMenuPermissions() {
        List<MenuPermissionDTO> result = menuPermissionService.getMenuPermissions();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据权限ID获取拥有该权限的角色列表
     */
    @GetMapping("/{permissionId}/roles")
    public ResponseEntity<List<RoleDTO>> getRolesByPermissionId(@PathVariable Long permissionId) {
        List<RoleDTO> result = menuPermissionService.getRolesByPermissionId(permissionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查权限标识是否存在
     */
    @GetMapping("/check-permission-key")
    public ResponseEntity<Boolean> checkPermissionKey(
            @RequestParam String permissionKey,
            @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = menuPermissionService.existsByPermissionKeyExcludeId(permissionKey, excludeId);
        } else {
            exists = menuPermissionService.existsByPermissionKey(permissionKey);
        }
        return ResponseEntity.ok(exists);
    }
} 