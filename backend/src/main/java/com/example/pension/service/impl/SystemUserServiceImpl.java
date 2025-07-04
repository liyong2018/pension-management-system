package com.example.pension.service.impl;

import com.example.pension.dao.SystemUserDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.SystemUserDTO;
import com.example.pension.dto.DirectorDTO;
import com.example.pension.dto.RoleDTO;
import com.example.pension.mapper.SystemUserDTOMapper;
import com.example.pension.mapper.RoleDTOMapper;
import com.example.pension.mapper.RoleMapper;
import com.example.pension.mapper.UserRoleMapper;
import com.example.pension.model.SystemUser;
import com.example.pension.model.Role;
import com.example.pension.model.Organization;
import com.example.pension.service.SystemUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SystemUserDTO create(SystemUserDTO systemUserDTO) {
        // 验证用户名是否重复
        SystemUser existingUser = systemUserDao.findByUsername(systemUserDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在：" + systemUserDTO.getUsername());
        }

        SystemUser systemUser = SystemUserDTOMapper.INSTANCE.toEntity(systemUserDTO);
        
        // 加密密码
        if (systemUserDTO.getPassword() != null) {
            systemUser.setPasswordHash(passwordEncoder.encode(systemUserDTO.getPassword()));
        }

        // 验证机构是否存在
        if (systemUserDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(systemUserDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + systemUserDTO.getOrganizationId());
            }
        }

        // 验证服务人员是否存在
        if (systemUserDTO.getServiceStaffId() != null) {
            // 这里可以添加对service_staff表的验证逻辑
            // ServiceStaff serviceStaff = serviceStaffDao.findById(systemUserDTO.getServiceStaffId());
            // if (serviceStaff == null) {
            //     throw new RuntimeException("关联的服务人员不存在，ID：" + systemUserDTO.getServiceStaffId());
            // }
        }

        // 验证服务人员是否存在
        if (systemUserDTO.getServiceStaffId() != null) {
            // 这里可以添加对service_staff表的验证逻辑
            // ServiceStaff serviceStaff = serviceStaffDao.findById(systemUserDTO.getServiceStaffId());
            // if (serviceStaff == null) {
            //     throw new RuntimeException("关联的服务人员不存在，ID：" + systemUserDTO.getServiceStaffId());
            // }
        }

        systemUserDao.insert(systemUser);

        // 分配角色
        if (systemUserDTO.getRoleIds() != null && !systemUserDTO.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(systemUser.getId(), systemUserDTO.getRoleIds());
        }

        return getById(systemUser.getId());
    }

    @Override
    public SystemUserDTO getById(Long id) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        
        SystemUserDTO dto = SystemUserDTOMapper.INSTANCE.toDTO(systemUser);
        
        // 获取用户角色
        List<Role> roles = roleMapper.findByUserId(id);
        dto.setRoles(RoleDTOMapper.INSTANCE.toDTO(roles));
        dto.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    public SystemUserDTO getByUsername(String username) {
        SystemUser systemUser = systemUserDao.findByUsername(username);
        if (systemUser == null) {
            return null;
        }
        return SystemUserDTOMapper.INSTANCE.toDTO(systemUser);
    }

    @Override
    public PageInfo<SystemUserDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<SystemUser> systemUsers = systemUserDao.findWithConditions(
                null, null, null, null, null, null);
        PageInfo<SystemUser> pageInfo = new PageInfo<>(systemUsers);
        
        List<SystemUserDTO> systemUserDTOs = SystemUserDTOMapper.INSTANCE.toDTO(systemUsers);
        
        // 为每个用户设置角色信息
        for (SystemUserDTO dto : systemUserDTOs) {
            List<Role> roles = roleMapper.findByUserId(dto.getId());
            dto.setRoles(RoleDTOMapper.INSTANCE.toDTO(roles));
            dto.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        }
        
        PageInfo<SystemUserDTO> result = new PageInfo<>();
        result.setList(systemUserDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public PageInfo<SystemUserDTO> searchByMultipleConditions(
            String username, String fullName, String email, Long organizationId,
            Boolean isAdmin, Boolean isActive, int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<SystemUser> systemUsers = systemUserDao.findWithConditions(
                username, fullName, email, organizationId, isAdmin, isActive);
        PageInfo<SystemUser> pageInfo = new PageInfo<>(systemUsers);
        
        List<SystemUserDTO> systemUserDTOs = SystemUserDTOMapper.INSTANCE.toDTO(systemUsers);
        
        // 为每个用户设置角色信息
        for (SystemUserDTO dto : systemUserDTOs) {
            List<Role> roles = roleMapper.findByUserId(dto.getId());
            dto.setRoles(RoleDTOMapper.INSTANCE.toDTO(roles));
            dto.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        }
        
        PageInfo<SystemUserDTO> result = new PageInfo<>();
        result.setList(systemUserDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public SystemUserDTO update(Long id, SystemUserDTO systemUserDTO) {
        SystemUser existingUser = systemUserDao.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }

        // 验证用户名是否重复（排除当前用户）
        if (systemUserDTO.getUsername() != null && 
            !systemUserDTO.getUsername().equals(existingUser.getUsername())) {
            SystemUser duplicateUser = systemUserDao.findByUsername(systemUserDTO.getUsername());
            if (duplicateUser != null) {
                throw new RuntimeException("用户名已存在：" + systemUserDTO.getUsername());
            }
        }

        // 更新字段
        SystemUserDTOMapper.INSTANCE.updateEntityFromDTO(systemUserDTO, existingUser);
        
        // 验证机构是否存在
        if (systemUserDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(systemUserDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + systemUserDTO.getOrganizationId());
            }
        }

        systemUserDao.update(existingUser);

        // 更新用户角色
        if (systemUserDTO.getRoleIds() != null) {
            updateUserRoles(id, systemUserDTO.getRoleIds());
        }
        
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(id);
        
        // 删除用户
        systemUserDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除列表不能为空");
        }
        
        // 删除用户角色关联
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }
        
        systemUserDao.deleteBatchByIds(ids);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, systemUser.getPasswordHash())) {
            throw new RuntimeException("当前密码错误");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        systemUserDao.updatePassword(id, encodedPassword);
    }

    @Override
    public void updateLastLoginTime(Long id) {
        systemUserDao.updateLastLoginTime(id, LocalDateTime.now());
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsert(userId, roleIds);
        }
    }

    @Override
    public void removeRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userRoleMapper.delete(userId, roleId);
            }
        }
    }

    @Override
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // 删除现有角色
        userRoleMapper.deleteByUserId(userId);
        
        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsert(userId, roleIds);
        }
    }

    @Override
    public List<SystemUserDTO> getUsersByRoleId(Long roleId) {
        List<SystemUser> systemUsers = systemUserDao.findByRoleId(roleId);
        return SystemUserDTOMapper.INSTANCE.toDTO(systemUsers);
    }

    @Override
    public Map<String, Long> getUserStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalUsers", (long) systemUserDao.findWithConditions(null, null, null, null, null, null).size());
        statistics.put("activeUsers", systemUserDao.countActiveUsers());
        statistics.put("adminUsers", systemUserDao.countAdminUsers());
        return statistics;
    }

    @Override
    public long countActiveUsers() {
        return systemUserDao.countActiveUsers();
    }

    @Override
    public long countAdminUsers() {
        return systemUserDao.countAdminUsers();
    }

    @Override
    public long countByOrganizationId(Long organizationId) {
        return systemUserDao.countByOrganizationId(organizationId);
    }

    @Override
    public void resetPassword(Long id) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        
        // 重置为默认密码：123456
        String defaultPassword = "123456";
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        systemUserDao.updatePassword(id, encodedPassword);
    }

    @Override
    public void toggleUserStatus(Long id) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        
        // 切换用户状态
        boolean newStatus = !systemUser.getIsActive();
        systemUserDao.updateActiveStatus(id, newStatus);
    }

    @Override
    public List<Map<String, Object>> getUserRoles(Long id) {
        SystemUser systemUser = systemUserDao.findById(id);
        if (systemUser == null) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }
        
        List<Role> roles = roleMapper.findByUserId(id);
        return roles.stream()
                .map(role -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("id", role.getId());
                    roleMap.put("name", role.getRoleName());
                    roleMap.put("description", role.getDescription());
                    return roleMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectorDTO> getAllDirectors() {
        return systemUserDao.findDirectorsByRole();
    }

    @Override
    public List<DirectorDTO> getDirectorsByOrganization(Long organizationId) {
        return systemUserDao.findDirectorsByOrganization(organizationId);
    }

    @Override
    public SystemUserDTO updateOwnProfile(Long userId, SystemUserDTO profileUpdateDto) {
        SystemUser existingUser = systemUserDao.findById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在，ID：" + userId);
        }

        boolean updated = false;
        if (profileUpdateDto.getFullName() != null && !profileUpdateDto.getFullName().equals(existingUser.getFullName())) {
            existingUser.setFullName(profileUpdateDto.getFullName());
            updated = true;
        }
        if (profileUpdateDto.getEmail() != null && !profileUpdateDto.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(profileUpdateDto.getEmail());
            updated = true;
        }
        if (profileUpdateDto.getPhone() != null && !profileUpdateDto.getPhone().equals(existingUser.getPhone())) {
            existingUser.setPhone(profileUpdateDto.getPhone());
            updated = true;
        }

        if (updated) {
            systemUserDao.update(existingUser);
        }
        
        return getById(userId); // 返回更新后的用户信息
    }
}