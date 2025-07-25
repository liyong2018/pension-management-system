package com.example.pension.service;

import com.example.pension.dto.SystemUserDTO;
import com.example.pension.dto.DirectorDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface SystemUserService {

    SystemUserDTO create(SystemUserDTO systemUserDTO);

    SystemUserDTO getById(Long id);

    SystemUserDTO getByUsername(String username);

    PageInfo<SystemUserDTO> getAll(int pageNum, int pageSize);

    PageInfo<SystemUserDTO> searchByMultipleConditions(
            String username,
            String fullName,
            String email,
            Long organizationId,
            Boolean isAdmin,
            Boolean isActive,
            int pageNum,
            int pageSize);

    SystemUserDTO update(Long id, SystemUserDTO systemUserDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    void updatePassword(Long id, String oldPassword, String newPassword);

    void updateLastLoginTime(Long id);

    // 用户角色管理
    void assignRoles(Long userId, List<Long> roleIds);

    void removeRoles(Long userId, List<Long> roleIds);

    void updateUserRoles(Long userId, List<Long> roleIds);

    List<SystemUserDTO> getUsersByRoleId(Long roleId);

    // 机构负责人管理
    /**
     * 获取所有机构负责人列表
     * @return 机构负责人列表
     */
    List<DirectorDTO> getAllDirectors();

    /**
     * 根据机构ID获取机构负责人列表
     * @param organizationId 机构ID
     * @return 机构负责人列表
     */
    List<DirectorDTO> getDirectorsByOrganization(Long organizationId);

    // 统计方法
    Map<String, Long> getUserStatistics();

    long countActiveUsers();

    long countAdminUsers();

    long countByOrganizationId(Long organizationId);

    // 额外的业务方法
    void resetPassword(Long id);

    void toggleUserStatus(Long id);

    List<Map<String, Object>> getUserRoles(Long id);

    SystemUserDTO updateOwnProfile(Long userId, SystemUserDTO profileUpdateDto);
} 