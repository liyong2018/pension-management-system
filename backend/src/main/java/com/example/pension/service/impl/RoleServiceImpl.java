package com.example.pension.service.impl;

import com.example.pension.dao.RoleDao;
import com.example.pension.dto.RoleDTO;
import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.mapper.RoleDTOMapper;
import com.example.pension.mapper.MenuPermissionDTOMapper;
import com.example.pension.mapper.MenuPermissionMapper;
import com.example.pension.mapper.RolePermissionMapper;
import com.example.pension.model.Role;
import com.example.pension.model.MenuPermission;
import com.example.pension.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private MenuPermissionMapper menuPermissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public RoleDTO create(RoleDTO roleDTO) {
        // 验证角色键是否重复
        if (roleDao.existsByRoleKey(roleDTO.getRoleKey())) {
            throw new RuntimeException("角色键已存在：" + roleDTO.getRoleKey());
        }

        Role role = RoleDTOMapper.INSTANCE.toEntity(roleDTO);
        roleDao.insert(role);

        // 分配权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            rolePermissionMapper.batchInsert(role.getId(), roleDTO.getPermissionIds());
        }

        return getById(role.getId());
    }

    @Override
    public RoleDTO getById(Long id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在，ID：" + id);
        }
        
        RoleDTO dto = RoleDTOMapper.INSTANCE.toDTO(role);
        
        // 获取角色权限
        List<MenuPermission> permissions = menuPermissionMapper.findByRoleId(id);
        dto.setPermissions(MenuPermissionDTOMapper.INSTANCE.toDTO(permissions));
        dto.setPermissionIds(permissions.stream().map(MenuPermission::getId).collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    public PageInfo<RoleDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles = roleDao.findAll();
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        
        List<RoleDTO> roleDTOs = RoleDTOMapper.INSTANCE.toDTO(roles);
        
        PageInfo<RoleDTO> result = new PageInfo<>(roleDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        
        return result;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleDao.findAll();
        return RoleDTOMapper.INSTANCE.toDTO(roles);
    }

    @Override
    public PageInfo<RoleDTO> searchByConditions(String roleName, String roleKey, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles = roleDao.findWithConditions(roleName, roleKey);
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        
        List<RoleDTO> roleDTOs = RoleDTOMapper.INSTANCE.toDTO(roles);
        
        PageInfo<RoleDTO> result = new PageInfo<>(roleDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        
        return result;
    }

    @Override
    public RoleDTO update(Long id, RoleDTO roleDTO) {
        Role existingRole = roleDao.findById(id);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在，ID：" + id);
        }

        // 验证角色键是否重复（排除当前角色）
        if (roleDao.existsByRoleKeyExcludeId(roleDTO.getRoleKey(), id)) {
            throw new RuntimeException("角色键已存在：" + roleDTO.getRoleKey());
        }

        Role role = RoleDTOMapper.INSTANCE.toEntity(roleDTO);
        role.setId(id);
        roleDao.update(role);

        // 更新权限
        if (roleDTO.getPermissionIds() != null) {
            updateRolePermissions(id, roleDTO.getPermissionIds());
        }

        return getById(id);
    }

    @Override
    public void delete(Long id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在，ID：" + id);
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(id);
        
        // 删除角色
        roleDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            // 删除角色权限关联
            rolePermissionMapper.deleteByRoleId(id);
        }
        
        // 批量删除角色
        roleDao.deleteBatchByIds(ids);
    }

    @Override
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleDao.findById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在，ID：" + roleId);
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleDao.findById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在，ID：" + roleId);
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                rolePermissionMapper.delete(roleId, permissionId);
            }
        }
    }

    @Override
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleDao.findById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在，ID：" + roleId);
        }

        // 删除现有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long userId) {
        List<Role> roles = roleDao.findByUserId(userId);
        return RoleDTOMapper.INSTANCE.toDTO(roles);
    }

    @Override
    public boolean existsByRoleKey(String roleKey) {
        return roleDao.existsByRoleKey(roleKey);
    }

    @Override
    public boolean existsByRoleKeyExcludeId(String roleKey, Long excludeId) {
        return roleDao.existsByRoleKeyExcludeId(roleKey, excludeId);
    }
} 