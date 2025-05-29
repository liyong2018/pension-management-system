package com.example.pension.dao;

import com.example.pension.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleDao {

    void insert(Role role);

    void update(Role role);

    void updateStatus(@Param("id") Long id, @Param("status") String status);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    Role findById(@Param("id") Long id);

    List<Role> findAll();

    List<Role> findWithConditions(@Param("roleName") String roleName,
                                  @Param("roleKey") String roleKey);

    List<Role> findByUserId(@Param("userId") Long userId);

    boolean existsByRoleKey(@Param("roleKey") String roleKey);

    boolean existsByRoleKeyExcludeId(@Param("roleKey") String roleKey, @Param("excludeId") Long excludeId);
} 