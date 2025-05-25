package com.example.pension.service;

import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.dto.RoleDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface MenuPermissionService {

    MenuPermissionDTO create(MenuPermissionDTO menuPermissionDTO);

    MenuPermissionDTO getById(Long id);

    PageInfo<MenuPermissionDTO> getAll(int pageNum, int pageSize);

    List<MenuPermissionDTO> getAllPermissions();

    List<MenuPermissionDTO> getPermissionTree();

    List<MenuPermissionDTO> getChildrenByParentId(Long parentId);

    PageInfo<MenuPermissionDTO> searchByConditions(
            String name,
            String type,
            String permissionKey,
            Boolean status,
            int pageNum,
            int pageSize);

    MenuPermissionDTO update(Long id, MenuPermissionDTO menuPermissionDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    // 权限相关查询
    List<MenuPermissionDTO> getPermissionsByRoleId(Long roleId);

    List<MenuPermissionDTO> getPermissionsByUserId(Long userId);

    List<MenuPermissionDTO> getMenuPermissions();

    // 根据权限ID获取拥有该权限的角色列表
    List<RoleDTO> getRolesByPermissionId(Long permissionId);

    // 验证方法
    boolean existsByPermissionKey(String permissionKey);

    boolean existsByPermissionKeyExcludeId(String permissionKey, Long excludeId);

    // 树形结构构建
    List<MenuPermissionDTO> buildPermissionTree(List<MenuPermissionDTO> permissions);
} 