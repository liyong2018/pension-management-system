package com.example.pension.dao;

import com.example.pension.model.SystemUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemUserDao {

    void insert(SystemUser systemUser);

    void update(SystemUser systemUser);

    void updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    void updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    void updateActiveStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    SystemUser findById(@Param("id") Long id);

    SystemUser findByUsername(@Param("username") String username);

    List<SystemUser> findWithConditions(@Param("username") String username,
                                       @Param("fullName") String fullName,
                                       @Param("email") String email,
                                       @Param("organizationId") Long organizationId,
                                       @Param("isAdmin") Boolean isAdmin,
                                       @Param("isActive") Boolean isActive);

    List<SystemUser> findByRoleId(@Param("roleId") Long roleId);

    Long countActiveUsers();

    Long countAdminUsers();

    Long countByOrganizationId(@Param("organizationId") Long organizationId);
} 