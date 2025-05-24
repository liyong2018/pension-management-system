package com.example.pension.service.impl;

import com.example.pension.dao.ElderlyFamilyMemberDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.dto.ElderlyProfileDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.exception.ValidationException;
import com.example.pension.mapper.ElderlyFamilyMemberDTOMapper;
import com.example.pension.mapper.ElderlyProfileMapper;
import com.example.pension.model.ElderlyFamilyMember;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.Organization;
import com.example.pension.service.ElderlyProfileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElderlyProfileServiceImpl implements ElderlyProfileService {

    private final ElderlyProfileDao elderlyProfileDao;
    private final OrganizationDao organizationDao;
    private final ElderlyProfileMapper elderlyProfileMapper;
    private final ElderlyFamilyMemberDao elderlyFamilyMemberDao;
    private final ElderlyFamilyMemberDTOMapper elderlyFamilyMemberMapper;

    private ElderlyProfileDTO mapToDTOWithFamilyMembers(ElderlyProfile elderlyProfile) {
        if (elderlyProfile == null) {
            return null;
        }
        ElderlyProfileDTO dto = elderlyProfileMapper.toDTO(elderlyProfile);

        if (dto != null && elderlyProfile.getId() != null) {
            List<ElderlyFamilyMember> familyMembers = elderlyFamilyMemberDao.findAllByElderlyId(elderlyProfile.getId());
            if (familyMembers != null && !familyMembers.isEmpty()) {
                dto.setFamilyMembers(elderlyFamilyMemberMapper.toDTO(familyMembers));
            } else {
                dto.setFamilyMembers(Collections.emptyList());
            }
        } else if (dto != null) {
            dto.setFamilyMembers(Collections.emptyList());
        }
        return dto;
    }

    private PageInfo<ElderlyProfileDTO> convertToDtoPageInfo(PageInfo<ElderlyProfile> entityPageInfo) {
        List<ElderlyProfileDTO> dtoList = entityPageInfo.getList().stream()
            .map(this::mapToDTOWithFamilyMembers)
            .collect(Collectors.toList());
        
        PageInfo<ElderlyProfileDTO> dtoPageInfo = new PageInfo<>(dtoList);
        dtoPageInfo.setPageNum(entityPageInfo.getPageNum());
        dtoPageInfo.setPageSize(entityPageInfo.getPageSize());
        dtoPageInfo.setTotal(entityPageInfo.getTotal());
        dtoPageInfo.setPages(entityPageInfo.getPages());
        dtoPageInfo.setNavigatePages(entityPageInfo.getNavigatePages());
        dtoPageInfo.setIsFirstPage(entityPageInfo.isIsFirstPage());
        dtoPageInfo.setIsLastPage(entityPageInfo.isIsLastPage());
        dtoPageInfo.setHasPreviousPage(entityPageInfo.isHasPreviousPage());
        dtoPageInfo.setHasNextPage(entityPageInfo.isHasNextPage());
        dtoPageInfo.setNavigatepageNums(entityPageInfo.getNavigatepageNums());
        dtoPageInfo.setNavigateFirstPage(entityPageInfo.getNavigateFirstPage());
        dtoPageInfo.setNavigateLastPage(entityPageInfo.getNavigateLastPage());
        
        return dtoPageInfo;
    }

    @Override
    @Transactional
    public ElderlyProfileDTO create(ElderlyProfileDTO elderlyProfileDTO) {
        if (elderlyProfileDTO.getIdCardNumber() != null &&
            !elderlyProfileDTO.getIdCardNumber().isEmpty() &&
            elderlyProfileDao.findByIdCardNumber(elderlyProfileDTO.getIdCardNumber()) != null) {
            throw new ValidationException("身份证号已存在");
        }

        ElderlyProfile elderlyProfile = elderlyProfileMapper.toEntity(elderlyProfileDTO);

        if (elderlyProfileDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(elderlyProfileDTO.getOrganizationId());
            if (organization == null) {
                throw new ResourceNotFoundException("机构不存在: " + elderlyProfileDTO.getOrganizationId());
            }
            elderlyProfile.setOrganization(organization);
        }

        elderlyProfileDao.insert(elderlyProfile);
        
        // 保存家属信息
        if (elderlyProfileDTO.getFamilyMembers() != null && !elderlyProfileDTO.getFamilyMembers().isEmpty()) {
            for (ElderlyFamilyMemberDTO familyMemberDTO : elderlyProfileDTO.getFamilyMembers()) {
                ElderlyFamilyMember familyMember = elderlyFamilyMemberMapper.toEntity(familyMemberDTO);
                familyMember.setElderlyProfile(elderlyProfile);
                elderlyFamilyMemberDao.insert(familyMember);
            }
        }
        
        return mapToDTOWithFamilyMembers(elderlyProfileDao.findById(elderlyProfile.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ElderlyProfileDTO getById(Long id) {
        ElderlyProfile elderlyProfile = elderlyProfileDao.findById(id);
        if (elderlyProfile == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + id);
        }
        return mapToDTOWithFamilyMembers(elderlyProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<ElderlyProfileDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ElderlyProfile> elderlyProfiles = elderlyProfileDao.findWithConditions(null, null, null, null, (pageNum -1) * pageSize, pageSize);
        PageInfo<ElderlyProfile> entityPageInfo = new PageInfo<>(elderlyProfiles);
        return convertToDtoPageInfo(entityPageInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<ElderlyProfileDTO> searchByMultipleConditions(
            String name,
            String idCardNumber,
            String phone,
            Long organizationId,
            int pageNum,
            int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ElderlyProfile> elderlyProfiles = elderlyProfileDao.findWithConditions(
                StringUtils.hasText(name) ? name : null,
                StringUtils.hasText(idCardNumber) ? idCardNumber : null,
                StringUtils.hasText(phone) ? phone : null,
                organizationId,
                (pageNum-1) * pageSize, 
                pageSize);
        PageInfo<ElderlyProfile> entityPageInfo = new PageInfo<>(elderlyProfiles);
        return convertToDtoPageInfo(entityPageInfo);
    }

    @Override
    @Transactional
    public ElderlyProfileDTO update(Long id, ElderlyProfileDTO elderlyProfileDTO) {
        ElderlyProfile elderlyProfile = elderlyProfileDao.findById(id);
        if (elderlyProfile == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + id);
        }

        if (elderlyProfileDTO.getIdCardNumber() != null &&
            !elderlyProfileDTO.getIdCardNumber().isEmpty() &&
            elderlyProfileDao.findByIdCardNumberAndIdNot(elderlyProfileDTO.getIdCardNumber(), id) != null) {
            throw new ValidationException("身份证号已被其他老人使用");
        }

        elderlyProfileMapper.updateEntityFromDTO(elderlyProfileDTO, elderlyProfile);

        if (elderlyProfileDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(elderlyProfileDTO.getOrganizationId());
            if (organization == null) {
                throw new ResourceNotFoundException("机构不存在: " + elderlyProfileDTO.getOrganizationId());
            }
            elderlyProfile.setOrganization(organization);
        } else {
            elderlyProfile.setOrganization(null);
        }

        elderlyProfileDao.update(elderlyProfile);
        
        // 更新家属信息：先删除所有现有的家属信息，然后插入新的
        elderlyFamilyMemberDao.deleteByElderlyId(id);
        if (elderlyProfileDTO.getFamilyMembers() != null && !elderlyProfileDTO.getFamilyMembers().isEmpty()) {
            for (ElderlyFamilyMemberDTO familyMemberDTO : elderlyProfileDTO.getFamilyMembers()) {
                ElderlyFamilyMember familyMember = elderlyFamilyMemberMapper.toEntity(familyMemberDTO);
                familyMember.setElderlyProfile(elderlyProfile);
                elderlyFamilyMemberDao.insert(familyMember);
            }
        }
        
        return mapToDTOWithFamilyMembers(elderlyProfileDao.findById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (elderlyProfileDao.findById(id) == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + id);
        }
        elderlyFamilyMemberDao.deleteByElderlyId(id);
        elderlyProfileDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            if (elderlyProfileDao.findById(id) == null) {
                throw new ResourceNotFoundException("尝试批量删除时，老人档案不存在: " + id);
            }
            elderlyFamilyMemberDao.deleteByElderlyId(id);
        }
        elderlyProfileDao.deleteBatchByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPensionTypeStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("居家养老", elderlyProfileDao.countByPensionType("居家养老"));
        statistics.put("社区养老", elderlyProfileDao.countByPensionType("社区养老"));
        statistics.put("机构养老", elderlyProfileDao.countByPensionType("机构养老"));
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAbilityAssessmentStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("能力完好", elderlyProfileDao.countByAbilityAssessment("能力完好"));
        statistics.put("轻度失能", elderlyProfileDao.countByAbilityAssessment("轻度失能"));
        statistics.put("中度失能", elderlyProfileDao.countByAbilityAssessment("中度失能"));
        statistics.put("重度失能", elderlyProfileDao.countByAbilityAssessment("重度失能"));
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElderlyProfileDTO> getByOrganizationId(Long organizationId) {
        List<ElderlyProfile> elderlyProfiles = elderlyProfileDao.findAllByOrganizationId(organizationId);
        return elderlyProfiles.stream()
                .map(this::mapToDTOWithFamilyMembers)
                .collect(Collectors.toList());
    }
} 