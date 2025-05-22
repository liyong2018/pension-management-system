package com.example.pension.repository;

import com.example.pension.model.ElderlyProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElderlyProfileRepository extends JpaRepository<ElderlyProfile, Long>, JpaSpecificationExecutor<ElderlyProfile> {
    
    Optional<ElderlyProfile> findByIdCardNumber(String idCardNumber);
    
    boolean existsByIdCardNumber(String idCardNumber);
    
    boolean existsByIdCardNumberAndIdNot(String idCardNumber, Long id);
    
    @Query("SELECT e FROM ElderlyProfile e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.idCardNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ElderlyProfile> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT e FROM ElderlyProfile e WHERE " +
           "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:idCardNumber IS NULL OR LOWER(e.idCardNumber) LIKE LOWER(CONCAT('%', :idCardNumber, '%'))) " +
           "AND (:phone IS NULL OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
           "AND (:community IS NULL OR LOWER(e.community) LIKE LOWER(CONCAT('%', :community, '%'))) " +
           "AND (:pensionType IS NULL OR e.pensionType = :pensionType)")
    Page<ElderlyProfile> findByMultipleConditions(
            @Param("name") String name,
            @Param("idCardNumber") String idCardNumber,
            @Param("phone") String phone,
            @Param("community") String community,
            @Param("pensionType") String pensionType,
            Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM ElderlyProfile e WHERE e.pensionType = :pensionType")
    Long countByPensionType(@Param("pensionType") String pensionType);
    
    @Query("SELECT COUNT(e) FROM ElderlyProfile e WHERE e.abilityAssessment = :abilityAssessment")
    Long countByAbilityAssessment(@Param("abilityAssessment") String abilityAssessment);
    
    List<ElderlyProfile> findByOrganizationId(Long organizationId);
} 