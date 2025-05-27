package com.example.pension.service;

import com.example.pension.dto.ElderlyProfileDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface ElderlyProfileService {
    
    ElderlyProfileDTO create(ElderlyProfileDTO elderlyProfileDTO);
    
    ElderlyProfileDTO getById(Long id);
    
    PageInfo<ElderlyProfileDTO> getAll(int pageNum, int pageSize);
    
    PageInfo<ElderlyProfileDTO> searchByMultipleConditions(
            String name, 
            String idCardNumber, 
            String phone, 
            String elderlyType,
            Long organizationId,
            int pageNum, 
            int pageSize);
    
    ElderlyProfileDTO update(Long id, ElderlyProfileDTO elderlyProfileDTO);
    
    void delete(Long id);
    
    void batchDelete(List<Long> ids);
    
    Map<String, Long> getPensionTypeStatistics();
    
    Map<String, Long> getAbilityAssessmentStatistics();
    
    List<ElderlyProfileDTO> getByOrganizationId(Long organizationId);
} 