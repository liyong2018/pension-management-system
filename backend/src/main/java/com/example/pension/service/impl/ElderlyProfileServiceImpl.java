package com.example.pension.service.impl;

import com.example.pension.dto.ElderlyProfileDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.exception.ValidationException;
import com.example.pension.mapper.ElderlyProfileMapper;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.Organization;
import com.example.pension.repository.ElderlyProfileRepository;
import com.example.pension.repository.OrganizationRepository;
import com.example.pension.service.ElderlyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElderlyProfileServiceImpl implements ElderlyProfileService {
    
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final ElderlyProfileMapper elderlyProfileMapper;
    
    @Override
    @Transactional
    public ElderlyProfileDTO create(ElderlyProfileDTO elderlyProfileDTO) {
        // 检查身份证号是否已存在
        if (elderlyProfileDTO.getIdCardNumber() != null && 
            !elderlyProfileDTO.getIdCardNumber().isEmpty() &&
            elderlyProfileRepository.existsByIdCardNumber(elderlyProfileDTO.getIdCardNumber())) {
            throw new ValidationException("身份证号已存在");
        }
        
        ElderlyProfile elderlyProfile = elderlyProfileMapper.toEntity(elderlyProfileDTO);
        
        // 设置机构关联
        if (elderlyProfileDTO.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(elderlyProfileDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("机构不存在"));
            elderlyProfile.setOrganization(organization);
        }
        
        ElderlyProfile savedElderlyProfile = elderlyProfileRepository.save(elderlyProfile);
        return elderlyProfileMapper.toDTO(savedElderlyProfile);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ElderlyProfileDTO getById(Long id) {
        ElderlyProfile elderlyProfile = elderlyProfileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("老人档案不存在"));
        return elderlyProfileMapper.toDTO(elderlyProfile);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ElderlyProfileDTO> getAll(Pageable pageable) {
        return elderlyProfileRepository.findAll(pageable)
            .map(elderlyProfileMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ElderlyProfileDTO> search(String keyword, Pageable pageable) {
        return elderlyProfileRepository.findByKeyword(keyword, pageable)
            .map(elderlyProfileMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ElderlyProfileDTO> searchByMultipleConditions(
            String name, 
            String idCardNumber, 
            String phone, 
            String community, 
            String pensionType, 
            Pageable pageable) {
        
        return elderlyProfileRepository.findByMultipleConditions(
                name, idCardNumber, phone, community, pensionType, pageable)
            .map(elderlyProfileMapper::toDTO);
    }
    
    @Override
    @Transactional
    public ElderlyProfileDTO update(Long id, ElderlyProfileDTO elderlyProfileDTO) {
        ElderlyProfile elderlyProfile = elderlyProfileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("老人档案不存在"));
        
        // 检查身份证号是否与其他记录冲突
        if (elderlyProfileDTO.getIdCardNumber() != null && 
            !elderlyProfileDTO.getIdCardNumber().isEmpty() &&
            elderlyProfileRepository.existsByIdCardNumberAndIdNot(elderlyProfileDTO.getIdCardNumber(), id)) {
            throw new ValidationException("身份证号已被其他老人使用");
        }
        
        elderlyProfileMapper.updateEntityFromDTO(elderlyProfileDTO, elderlyProfile);
        
        // 更新机构关联
        if (elderlyProfileDTO.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(elderlyProfileDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("机构不存在"));
            elderlyProfile.setOrganization(organization);
        } else {
            elderlyProfile.setOrganization(null);
        }
        
        ElderlyProfile updatedElderlyProfile = elderlyProfileRepository.save(elderlyProfile);
        return elderlyProfileMapper.toDTO(updatedElderlyProfile);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (!elderlyProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("老人档案不存在");
        }
        elderlyProfileRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        List<ElderlyProfile> elderlyProfiles = elderlyProfileRepository.findAllById(ids);
        elderlyProfileRepository.deleteAll(elderlyProfiles);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPensionTypeStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("居家养老", elderlyProfileRepository.countByPensionType("居家养老"));
        statistics.put("社区养老", elderlyProfileRepository.countByPensionType("社区养老"));
        statistics.put("机构养老", elderlyProfileRepository.countByPensionType("机构养老"));
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAbilityAssessmentStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("能力完好", elderlyProfileRepository.countByAbilityAssessment("能力完好"));
        statistics.put("轻度失能", elderlyProfileRepository.countByAbilityAssessment("轻度失能"));
        statistics.put("中度失能", elderlyProfileRepository.countByAbilityAssessment("中度失能"));
        statistics.put("重度失能", elderlyProfileRepository.countByAbilityAssessment("重度失能"));
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ElderlyProfileDTO> getByOrganizationId(Long organizationId) {
        List<ElderlyProfile> elderlyProfiles = elderlyProfileRepository.findByOrganizationId(organizationId);
        return elderlyProfiles.stream()
                .map(elderlyProfileMapper::toDTO)
                .collect(Collectors.toList());
    }
} 