package com.example.pension.service;

import com.example.pension.dto.RoleDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RoleService {

    RoleDTO create(RoleDTO roleDTO);

    RoleDTO getById(Long id);

    PageInfo<RoleDTO> getAll(int pageNum, int pageSize);

    List<RoleDTO> getAllRoles();

    PageInfo<RoleDTO> searchByConditions(
            String roleName,
            String roleKey,
            int pageNum,
            int pageSize);

    RoleDTO update(Long id, RoleDTO roleDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    // 角色权限管理
    void assignPermissions(Long roleId, List<Long> permissionIds);

    void removePermissions(Long roleId, List<Long> permissionIds);

    void updateRolePermissions(Long roleId, List<Long> permissionIds);

    // 获取角色权限ID列表
    List<Long> getRolePermissions(Long roleId);

    List<RoleDTO> getRolesByUserId(Long userId);

    // 验证方法
    boolean existsByRoleKey(String roleKey);

    boolean existsByRoleKeyExcludeId(String roleKey, Long excludeId);
} 