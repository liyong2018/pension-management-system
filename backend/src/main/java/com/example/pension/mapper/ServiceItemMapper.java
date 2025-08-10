package com.example.pension.mapper;

import com.example.pension.model.ServiceItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 服务项目Mapper接口
 */
@Mapper
public interface ServiceItemMapper {
    
    /**
     * 插入服务项目
     */
    @Insert("INSERT INTO service_items (title, description, category, price, duration, image_url, tags, " +
            "organization_id, organization_name, status, rating, review_count, service_area, service_time, " +
            "emergency_support, requirements, created_at, updated_at, created_by, updated_by, remarks) " +
            "VALUES (#{title}, #{description}, #{category}, #{price}, #{duration}, #{imageUrl}, #{tags}, " +
            "#{organizationId}, #{organizationName}, #{status}, #{rating}, #{reviewCount}, #{serviceArea}, " +
            "#{serviceTime}, #{emergencySupport}, #{requirements}, #{createdAt}, #{updatedAt}, #{createdBy}, " +
            "#{updatedBy}, #{remarks})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceItem serviceItem);
    
    /**
     * 根据ID查询服务项目
     */
    @Select("SELECT * FROM service_items WHERE id = #{id}")
    ServiceItem selectById(Long id);
    
    /**
     * 更新服务项目
     */
    @Update("UPDATE service_items SET title = #{title}, description = #{description}, category = #{category}, " +
            "price = #{price}, duration = #{duration}, image_url = #{imageUrl}, tags = #{tags}, " +
            "organization_id = #{organizationId}, organization_name = #{organizationName}, status = #{status}, " +
            "rating = #{rating}, review_count = #{reviewCount}, service_area = #{serviceArea}, " +
            "service_time = #{serviceTime}, emergency_support = #{emergencySupport}, requirements = #{requirements}, " +
            "updated_at = #{updatedAt}, updated_by = #{updatedBy}, remarks = #{remarks} WHERE id = #{id}")
    int update(ServiceItem serviceItem);
    
    /**
     * 根据ID删除服务项目
     */
    @Delete("DELETE FROM service_items WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询所有服务项目（分页）
     */
    @Select("<script>" +
            "SELECT si.*, o.name as organization_name FROM service_items si " +
            "LEFT JOIN organizations o ON si.organization_id = o.id " +
            "WHERE 1=1 " +
            "<if test='category != null and category != \"\"'>" +
            "AND si.category = #{category} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (si.title LIKE CONCAT('%', #{keyword}, '%') OR si.description LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='organizationId != null'>" +
            "AND si.organization_id = #{organizationId} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND si.status = #{status} " +
            "</if>" +
            "ORDER BY si.created_at DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<ServiceItem> selectByPage(@Param("category") String category,
                                   @Param("keyword") String keyword,
                                   @Param("organizationId") Long organizationId,
                                   @Param("status") String status,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    
    /**
     * 统计服务项目总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM service_items si " +
            "WHERE 1=1 " +
            "<if test='category != null and category != \"\"'>" +
            "AND si.category = #{category} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (si.title LIKE CONCAT('%', #{keyword}, '%') OR si.description LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='organizationId != null'>" +
            "AND si.organization_id = #{organizationId} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND si.status = #{status} " +
            "</if>" +
            "</script>")
    long countByConditions(@Param("category") String category,
                          @Param("keyword") String keyword,
                          @Param("organizationId") Long organizationId,
                          @Param("status") String status);
    
    /**
     * 根据机构ID查询服务项目
     */
    @Select("SELECT * FROM service_items WHERE organization_id = #{organizationId} AND status = 'ACTIVE' ORDER BY created_at DESC")
    List<ServiceItem> selectByOrganizationId(Long organizationId);
    
    /**
     * 根据分类查询服务项目
     */
    @Select("SELECT * FROM service_items WHERE category = #{category} AND status = 'ACTIVE' ORDER BY rating DESC, created_at DESC LIMIT #{limit}")
    List<ServiceItem> selectByCategory(@Param("category") String category, @Param("limit") int limit);
    
    /**
     * 获取推荐服务项目（按评分排序）
     */
    @Select("SELECT * FROM service_items WHERE status = 'ACTIVE' ORDER BY rating DESC, review_count DESC LIMIT #{limit}")
    List<ServiceItem> selectRecommended(@Param("limit") int limit);
}