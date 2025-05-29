package com.example.pension.controller;

import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.dto.RoleDTO;
import com.example.pension.service.MenuPermissionService;
import com.example.pension.service.impl.MenuPermissionServiceImpl;
import com.example.pension.service.RoleService;
import com.example.pension.dao.UserDao;
import com.example.pension.model.User;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class MenuPermissionController {

    @Autowired
    private MenuPermissionService menuPermissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDao userDao;

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
     * 获取当前用户的菜单权限树
     */
    @GetMapping("/user-menu-tree")
    public ResponseEntity<List<MenuPermissionDTO>> getUserMenuTree() {
        log.info("开始获取用户菜单权限树");
        
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("当前认证信息: {}", authentication);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("用户未认证");
            throw new RuntimeException("用户未认证");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("当前用户: {}, 权限: {}", userDetails.getUsername(), userDetails.getAuthorities());
        
        User user = userDao.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("当前用户不存在: " + userDetails.getUsername()));
        
        log.info("找到用户: ID={}, 用户名={}, 是否管理员={}", user.getId(), user.getUsername(), user.isAdmin());
        
        List<MenuPermissionDTO> result = menuPermissionService.getPermissionsByUserId(user.getId());
        log.info("用户权限数量: {}", result != null ? result.size() : 0);
        
        List<MenuPermissionDTO> menuTree = menuPermissionService.buildPermissionTree(result);
        log.info("构建的菜单树节点数量: {}", menuTree != null ? menuTree.size() : 0);
        
        return ResponseEntity.ok(menuTree);
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

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugPermissions() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            // 获取所有权限数据
            List<MenuPermissionDTO> allPermissions = menuPermissionService.getAllPermissions();
            debugInfo.put("totalPermissions", allPermissions.size());
            debugInfo.put("permissions", allPermissions);
            
            // 尝试构建权限树
            List<MenuPermissionDTO> tree = menuPermissionService.getPermissionTree();
            debugInfo.put("treeSize", tree.size());
            debugInfo.put("tree", tree);
            
            debugInfo.put("status", "success");
        } catch (Exception e) {
            log.error("权限调试接口出错", e);
            debugInfo.put("status", "error");
            debugInfo.put("error", e.getMessage());
            debugInfo.put("errorClass", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(debugInfo);
    }

    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查是否已有权限数据
            List<MenuPermissionDTO> existing = menuPermissionService.getAllPermissions();
            if (!existing.isEmpty()) {
                result.put("status", "已存在权限数据");
                result.put("count", existing.size());
                return ResponseEntity.ok(result);
            }
            
            // 这里可以添加初始化逻辑
            result.put("status", "需要运行数据库初始化脚本");
            result.put("message", "请运行schema_fixed.sql中的权限数据初始化部分");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("权限初始化接口出错", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 直接使用DAO检查数据库
            List<MenuPermissionDTO> allPermissions = menuPermissionService.getAllPermissions();
            status.put("hasPermissions", !allPermissions.isEmpty());
            status.put("permissionCount", allPermissions.size());
            
            if (allPermissions.isEmpty()) {
                status.put("message", "权限表为空，需要执行数据库初始化脚本");
                status.put("sqlScript", "请运行 backend/src/main/resources/schema_fixed.sql 中的权限数据部分");
            } else {
                status.put("message", "权限数据正常");
                // 显示前几个权限作为示例
                status.put("samplePermissions", allPermissions.stream()
                    .limit(5)
                    .map(p -> p.getName() + " (" + p.getPermissionKey() + ")")
                    .collect(java.util.stream.Collectors.toList()));
            }
            
            status.put("status", "success");
        } catch (Exception e) {
            log.error("数据库状态检查失败", e);
            status.put("status", "error");
            status.put("error", e.getMessage());
            status.put("errorType", e.getClass().getSimpleName());
            
            // 检查是否是连接问题
            if (e.getMessage() != null && e.getMessage().contains("Connection")) {
                status.put("suggestion", "请检查数据库连接配置");
            } else if (e.getMessage() != null && e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")) {
                status.put("suggestion", "请运行数据库建表脚本 schema_fixed.sql");
            } else {
                status.put("suggestion", "请检查数据库配置和权限表结构");
            }
        }
        
        return ResponseEntity.ok(status);
    }

    /**
     * 切换权限状态（启用/禁用）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> togglePermissionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean status = request.get("status");
        
        try {
            // 使用专门的状态更新方法
            ((MenuPermissionServiceImpl) menuPermissionService).updateStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("更新权限状态失败", e);
            throw new RuntimeException("更新权限状态失败: " + e.getMessage());
        }
    }

    /**
     * 切换权限显示状态（显示/隐藏）
     */
    @PutMapping("/{id}/visible")
    public ResponseEntity<Void> togglePermissionVisible(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean isVisible = request.get("isVisible");
        
        try {
            // 使用专门的显示状态更新方法
            ((MenuPermissionServiceImpl) menuPermissionService).updateVisible(id, isVisible);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("更新权限显示状态失败", e);
            throw new RuntimeException("更新权限显示状态失败: " + e.getMessage());
        }
    }
} 