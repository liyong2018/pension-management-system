package com.example.pension.mapper;

import com.example.pension.model.MenuPermission;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuPermissionMapper {

    @Select("SELECT * FROM menu_permission WHERE id = #{id}")
    MenuPermission findById(Long id);

    @Select("SELECT * FROM menu_permission ORDER BY sort_order ASC, create_time DESC")
    List<MenuPermission> findAll();

    @Select("SELECT * FROM menu_permission WHERE parent_id IS NULL ORDER BY sort_order ASC")
    List<MenuPermission> findRootPermissions();

    @Select("SELECT * FROM menu_permission WHERE parent_id = #{parentId} ORDER BY sort_order ASC")
    List<MenuPermission> findByParentId(Long parentId);

    @Select("<script>" +
            "SELECT * FROM menu_permission WHERE 1=1 " +
            "<if test='name != null and name != \"\"'>" +
            "AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "<if test='permissionKey != null and permissionKey != \"\"'>" +
            "AND permission_key LIKE CONCAT('%', #{permissionKey}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY sort_order ASC, create_time DESC " +
            "</script>")
    List<MenuPermission> findByConditions(@Param("name") String name,
                                          @Param("type") String type,
                                          @Param("permissionKey") String permissionKey,
                                          @Param("status") Boolean status);

    @Insert("INSERT INTO menu_permission (parent_id, name, type, permission_key, route_path, " +
            "component_path, icon, sort_order, is_visible, status, remark, create_time, update_time) " +
            "VALUES (#{parentId}, #{name}, #{type}, #{permissionKey}, #{routePath}, " +
            "#{componentPath}, #{icon}, #{sortOrder}, #{isVisible}, #{status}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MenuPermission menuPermission);

    @Update("UPDATE menu_permission SET " +
            "parent_id = #{parentId}, " +
            "name = #{name}, " +
            "type = #{type}, " +
            "permission_key = #{permissionKey}, " +
            "route_path = #{routePath}, " +
            "component_path = #{componentPath}, " +
            "icon = #{icon}, " +
            "sort_order = #{sortOrder}, " +
            "is_visible = #{isVisible}, " +
            "status = #{status}, " +
            "remark = #{remark}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void update(MenuPermission menuPermission);

    @Delete("DELETE FROM menu_permission WHERE id = #{id}")
    void deleteById(Long id);

    @Delete("<script>" +
            "DELETE FROM menu_permission WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchDeleteByIds(@Param("ids") List<Long> ids);

    // 根据角色ID查询权限
    @Select("SELECT mp.* FROM menu_permission mp " +
            "INNER JOIN role_permission rp ON mp.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} " +
            "ORDER BY mp.sort_order ASC")
    List<MenuPermission> findByRoleId(Long roleId);

    // 根据用户ID查询权限（通过角色关联）
    @Select("SELECT DISTINCT mp.* FROM menu_permission mp " +
            "INNER JOIN role_permission rp ON mp.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND mp.status = true " +
            "ORDER BY mp.sort_order ASC")
    List<MenuPermission> findByUserId(Long userId);

    // 查询菜单类型的权限（用于构建菜单树）
    @Select("SELECT * FROM menu_permission WHERE type = 'MENU' AND status = true ORDER BY sort_order ASC")
    List<MenuPermission> findMenuPermissions();

    // 检查权限标识符是否存在
    @Select("SELECT COUNT(*) FROM menu_permission WHERE permission_key = #{permissionKey} AND id != #{excludeId}")
    int countByPermissionKeyExcludeId(@Param("permissionKey") String permissionKey, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM menu_permission WHERE permission_key = #{permissionKey}")
    int countByPermissionKey(String permissionKey);
} 