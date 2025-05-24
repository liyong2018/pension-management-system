package com.example.pension.dao;

import com.example.pension.model.MenuPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuPermissionDao {

    void insert(MenuPermission menuPermission);

    void update(MenuPermission menuPermission);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    MenuPermission findById(@Param("id") Long id);

    List<MenuPermission> findAll();

    List<MenuPermission> findRootPermissions();

    List<MenuPermission> findByParentId(@Param("parentId") Long parentId);

    List<MenuPermission> findWithConditions(@Param("name") String name,
                                           @Param("type") String type,
                                           @Param("permissionKey") String permissionKey,
                                           @Param("status") Boolean status);

    List<MenuPermission> findByRoleId(@Param("roleId") Long roleId);

    List<MenuPermission> findByUserId(@Param("userId") Long userId);

    List<MenuPermission> findMenuPermissions();

    boolean existsByPermissionKey(@Param("permissionKey") String permissionKey);

    boolean existsByPermissionKeyExcludeId(@Param("permissionKey") String permissionKey, @Param("excludeId") Long excludeId);
} 