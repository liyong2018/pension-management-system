package com.example.pension.service;

import com.example.pension.dto.ElderlyProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ElderlyProfileService {
    
    ElderlyProfileDTO create(ElderlyProfileDTO elderlyProfileDTO);
    
    ElderlyProfileDTO getById(Long id);
    
    Page<ElderlyProfileDTO> getAll(Pageable pageable);
    
    Page<ElderlyProfileDTO> search(String keyword, Pageable pageable);
    
    Page<ElderlyProfileDTO> searchByMultipleConditions(
            String name, 
            String idCardNumber, 
            String phone, 
            String community, 
            String pensionType, 
            Pageable pageable);
    
    ElderlyProfileDTO update(Long id, ElderlyProfileDTO elderlyProfileDTO);
    
    void delete(Long id);
    
    void batchDelete(List<Long> ids);
    
    Map<String, Long> getPensionTypeStatistics();
    
    Map<String, Long> getAbilityAssessmentStatistics();
    
    List<ElderlyProfileDTO> getByOrganizationId(Long organizationId);
} 