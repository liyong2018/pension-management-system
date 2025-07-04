package com.example.pension.service.impl;

import com.example.pension.dao.MenuPermissionDao;
import com.example.pension.dao.RoleDao;
import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.dto.RoleDTO;
import com.example.pension.mapper.MenuPermissionDTOMapper;
import com.example.pension.mapper.RoleDTOMapper;
import com.example.pension.mapper.RolePermissionMapper;
import com.example.pension.model.MenuPermission;
import com.example.pension.model.Role;
import com.example.pension.service.MenuPermissionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuPermissionServiceImpl implements MenuPermissionService {

    @Autowired
    private MenuPermissionDao menuPermissionDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public MenuPermissionDTO create(MenuPermissionDTO menuPermissionDTO) {
        // 验证权限标识是否重复（只有当权限标识不为空时才检查）
        if (menuPermissionDTO.getPermissionKey() != null && 
            !menuPermissionDTO.getPermissionKey().trim().isEmpty() &&
            menuPermissionDao.existsByPermissionKey(menuPermissionDTO.getPermissionKey())) {
            throw new RuntimeException("权限标识已存在：" + menuPermissionDTO.getPermissionKey());
        }

        // 验证父权限是否存在
        if (menuPermissionDTO.getParentId() != null) {
            MenuPermission parentPermission = menuPermissionDao.findById(menuPermissionDTO.getParentId());
            if (parentPermission == null) {
                throw new RuntimeException("父权限不存在，ID：" + menuPermissionDTO.getParentId());
            }
        }

        MenuPermission menuPermission = MenuPermissionDTOMapper.INSTANCE.toEntity(menuPermissionDTO);
        menuPermissionDao.insert(menuPermission);

        return getById(menuPermission.getId());
    }

    @Override
    public MenuPermissionDTO getById(Long id) {
        MenuPermission menuPermission = menuPermissionDao.findById(id);
        if (menuPermission == null) {
            throw new RuntimeException("权限不存在，ID：" + id);
        }
        
        return MenuPermissionDTOMapper.INSTANCE.toDTO(menuPermission);
    }

    @Override
    public PageInfo<MenuPermissionDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MenuPermission> menuPermissions = menuPermissionDao.findAll();
        PageInfo<MenuPermission> pageInfo = new PageInfo<>(menuPermissions);
        
        List<MenuPermissionDTO> menuPermissionDTOs = MenuPermissionDTOMapper.INSTANCE.toDTO(menuPermissions);
        
        PageInfo<MenuPermissionDTO> result = new PageInfo<>();
        result.setList(menuPermissionDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public List<MenuPermissionDTO> getAllPermissions() {
        List<MenuPermission> menuPermissions = menuPermissionDao.findAll();
        return MenuPermissionDTOMapper.INSTANCE.toDTO(menuPermissions);
    }

    @Override
    public List<MenuPermissionDTO> getPermissionTree() {
        List<MenuPermission> allPermissions = menuPermissionDao.findAll();
        List<MenuPermissionDTO> allPermissionDTOs = MenuPermissionDTOMapper.INSTANCE.toDTO(allPermissions);
        return buildPermissionTree(allPermissionDTOs);
    }

    @Override
    public List<MenuPermissionDTO> getChildrenByParentId(Long parentId) {
        List<MenuPermission> children = menuPermissionDao.findByParentId(parentId);
        return MenuPermissionDTOMapper.INSTANCE.toDTO(children);
    }

    @Override
    public PageInfo<MenuPermissionDTO> searchByConditions(
            String name, String type, String permissionKey, Boolean status,
            int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize);
        List<MenuPermission> menuPermissions = menuPermissionDao.findWithConditions(name, type, permissionKey, status);
        PageInfo<MenuPermission> pageInfo = new PageInfo<>(menuPermissions);
        
        List<MenuPermissionDTO> menuPermissionDTOs = MenuPermissionDTOMapper.INSTANCE.toDTO(menuPermissions);
        
        PageInfo<MenuPermissionDTO> result = new PageInfo<>();
        result.setList(menuPermissionDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public MenuPermissionDTO update(Long id, MenuPermissionDTO menuPermissionDTO) {
        MenuPermission existingPermission = menuPermissionDao.findById(id);
        if (existingPermission == null) {
            throw new RuntimeException("权限不存在，ID：" + id);
        }

        // 验证权限标识是否重复（排除当前记录，只有当权限标识不为空时才检查）
        if (menuPermissionDTO.getPermissionKey() != null && 
            !menuPermissionDTO.getPermissionKey().trim().isEmpty() &&
            menuPermissionDao.existsByPermissionKeyExcludeId(menuPermissionDTO.getPermissionKey(), id)) {
            throw new RuntimeException("权限标识已存在：" + menuPermissionDTO.getPermissionKey());
        }

        // 验证父权限是否存在
        if (menuPermissionDTO.getParentId() != null) {
            MenuPermission parentPermission = menuPermissionDao.findById(menuPermissionDTO.getParentId());
            if (parentPermission == null) {
                throw new RuntimeException("父权限不存在，ID：" + menuPermissionDTO.getParentId());
            }
            
            // 不能将自己设置为父权限
            if (menuPermissionDTO.getParentId().equals(id)) {
                throw new RuntimeException("不能将自己设置为父权限");
            }
        }

        MenuPermission menuPermission = MenuPermissionDTOMapper.INSTANCE.toEntity(menuPermissionDTO);
        menuPermission.setId(id);
        menuPermissionDao.update(menuPermission);

        return getById(id);
    }

    @Override
    public void delete(Long id) {
        MenuPermission menuPermission = menuPermissionDao.findById(id);
        if (menuPermission == null) {
            throw new RuntimeException("权限不存在，ID：" + id);
        }

        // 检查是否有子权限
        List<MenuPermission> children = menuPermissionDao.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子权限，无法删除");
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByPermissionId(id);
        
        // 删除权限
        menuPermissionDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            // 检查是否有子权限
            List<MenuPermission> children = menuPermissionDao.findByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("权限ID " + id + " 存在子权限，无法删除");
            }
            
            // 删除角色权限关联
            rolePermissionMapper.deleteByPermissionId(id);
        }
        
        // 批量删除权限
        menuPermissionDao.deleteBatchByIds(ids);
    }

    @Override
    public List<MenuPermissionDTO> getPermissionsByRoleId(Long roleId) {
        List<MenuPermission> permissions = menuPermissionDao.findByRoleId(roleId);
        return MenuPermissionDTOMapper.INSTANCE.toDTO(permissions);
    }

    @Override
    public List<MenuPermissionDTO> getPermissionsByUserId(Long userId) {
        List<MenuPermission> permissions = menuPermissionDao.findByUserId(userId);
        return MenuPermissionDTOMapper.INSTANCE.toDTO(permissions);
    }

    @Override
    public List<MenuPermissionDTO> getMenuPermissions() {
        List<MenuPermission> menuPermissions = menuPermissionDao.findMenuPermissions();
        List<MenuPermissionDTO> menuPermissionDTOs = MenuPermissionDTOMapper.INSTANCE.toDTO(menuPermissions);
        return buildPermissionTree(menuPermissionDTOs);
    }

    @Override
    public boolean existsByPermissionKey(String permissionKey) {
        return menuPermissionDao.existsByPermissionKey(permissionKey);
    }

    @Override
    public boolean existsByPermissionKeyExcludeId(String permissionKey, Long excludeId) {
        return menuPermissionDao.existsByPermissionKeyExcludeId(permissionKey, excludeId);
    }

    @Override
    public List<MenuPermissionDTO> buildPermissionTree(List<MenuPermissionDTO> permissions) {
        // 按父ID分组
        Map<Long, List<MenuPermissionDTO>> groupedByParent = permissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));

        // 构建树形结构
        List<MenuPermissionDTO> rootPermissions = groupedByParent.getOrDefault(0L, new ArrayList<>());
        
        for (MenuPermissionDTO permission : rootPermissions) {
            setChildren(permission, groupedByParent);
        }
        
        return rootPermissions;
    }

    private void setChildren(MenuPermissionDTO parent, Map<Long, List<MenuPermissionDTO>> groupedByParent) {
        List<MenuPermissionDTO> children = groupedByParent.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (MenuPermissionDTO child : children) {
                setChildren(child, groupedByParent);
            }
        }
    }

    @Override
    public List<RoleDTO> getRolesByPermissionId(Long permissionId) {
        // 验证权限是否存在
        MenuPermission permission = menuPermissionDao.findById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在，ID：" + permissionId);
        }
        
        // 获取拥有该权限的角色ID列表
        List<Long> roleIds = rolePermissionMapper.findRoleIdsByPermissionId(permissionId);
        
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据角色ID列表获取角色信息
        List<Role> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            Role role = roleDao.findById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        
        return RoleDTOMapper.INSTANCE.toDTO(roles);
    }

    /**
     * 更新权限状态
     */
    public void updateStatus(Long id, Boolean status) {
        MenuPermission permission = menuPermissionDao.findById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在，ID：" + id);
        }
        menuPermissionDao.updateStatus(id, status);
    }

    /**
     * 更新权限显示状态
     */
    public void updateVisible(Long id, Boolean isVisible) {
        MenuPermission permission = menuPermissionDao.findById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在，ID：" + id);
        }
        menuPermissionDao.updateVisible(id, isVisible);
    }
}