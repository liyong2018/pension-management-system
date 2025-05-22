package com.example.pension.dao;

import com.example.pension.model.ElderlyProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElderlyProfileDao {
    ElderlyProfile findById(@Param("id") Long id);

    List<ElderlyProfile> findWithConditions(
            @Param("name") String name,
            @Param("idCardNumber") String idCardNumber,
            @Param("phone") String phone,
            @Param("organizationId") Long organizationId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countWithConditions(
            @Param("name") String name,
            @Param("idCardNumber") String idCardNumber,
            @Param("phone") String phone,
            @Param("organizationId") Long organizationId);

    int insert(ElderlyProfile elderlyProfile);

    int update(ElderlyProfile elderlyProfile);

    int deleteById(@Param("id") Long id);

    List<ElderlyProfile> findAllByOrganizationId(@Param("organizationId") Long organizationId);

    // For statistics
    long countByPensionType(@Param("pensionType") String pensionType);
    long countByAbilityAssessment(@Param("abilityAssessment") String abilityAssessment);

    // For validation
    ElderlyProfile findByIdCardNumber(@Param("idCardNumber") String idCardNumber);
    ElderlyProfile findByIdCardNumberAndIdNot(@Param("idCardNumber") String idCardNumber, @Param("id") Long id);

    // For batch delete
    int deleteBatchByIds(@Param("ids") List<Long> ids);
} 