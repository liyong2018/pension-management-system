package com.example.pension.dao;

import com.example.pension.model.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationDao {
    Organization findById(@Param("id") Long id);

    List<Organization> findAll(); // 简单示例，后续需要支持分页和过滤

    // MyBatis 通常需要显式定义分页和过滤参数
    List<Organization> findWithConditions(
            @Param("name") String name,
            @Param("page") int page,
            @Param("size") int size);

    long countWithConditions(@Param("name") String name);

    int insert(Organization organization);

    int update(Organization organization);

    int deleteById(@Param("id") Long id);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

} 