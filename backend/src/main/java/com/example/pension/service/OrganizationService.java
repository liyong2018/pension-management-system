package com.example.pension.service;

import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.mapper.OrganizationMapper;
import com.example.pension.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationDao organizationDao;
    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationService(OrganizationDao organizationDao, OrganizationMapper organizationMapper) {
        this.organizationDao = organizationDao;
        this.organizationMapper = organizationMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrganizationDTO> getAllOrganizations(Pageable pageable, String name) {
        List<Organization> organizations;
        
        // 根据名称搜索或获取所有
        if (StringUtils.hasText(name)) {
            organizations = organizationDao.findByNameContaining(name);
        } else {
            organizations = organizationDao.findAll();
        }

        // 应用排序
        Sort sort = pageable.getSort();
        if (sort != null && sort.isSorted()) {
            organizations = sortOrganizations(organizations, sort);
        }

        // 应用分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), organizations.size());
        List<Organization> pageContent = start < organizations.size() ? 
            organizations.subList(start, end) : new ArrayList<>();

        // 转换为DTO
        List<OrganizationDTO> dtoList = pageContent.stream()
                .map(organizationMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, organizations.size());
    }

    private List<Organization> sortOrganizations(List<Organization> organizations, Sort sort) {
        return organizations.stream()
                .sorted((o1, o2) -> {
                    for (Sort.Order order : sort) {
                        int comparison = compareOrganizations(o1, o2, order.getProperty());
                        if (comparison != 0) {
                            return order.isAscending() ? comparison : -comparison;
                        }
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    private int compareOrganizations(Organization o1, Organization o2, String property) {
        switch (property) {
            case "id":
                return o1.getId().compareTo(o2.getId());
            case "name":
                return o1.getName().compareTo(o2.getName());
            case "type":
                return compareNullableStrings(o1.getType(), o2.getType());
            case "address":
                return compareNullableStrings(o1.getAddress(), o2.getAddress());
            default:
                return 0;
        }
    }

    private int compareNullableStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> getOrganizationById(Long id) {
        Organization organization = organizationDao.findById(id);
        return Optional.ofNullable(organization).map(organizationMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> getOrganizationByName(String name) {
        return organizationDao.findByName(name).map(organizationMapper::toDto);
    }

    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationDTO createOrganizationDTO) {
        if (organizationDao.findByName(createOrganizationDTO.getName()).isPresent()) {
            throw new DataIntegrityViolationException("同名机构已存在: " + createOrganizationDTO.getName());
        }
        Organization organization = organizationMapper.toEntity(createOrganizationDTO);
        organizationDao.save(organization);
        Organization savedOrganization = organizationDao.findById(organization.getId());
        return organizationMapper.toDto(savedOrganization);
    }

    @Transactional
    public OrganizationDTO updateOrganization(Long id, UpdateOrganizationDTO updateOrganizationDTO) {
        Organization organization = organizationDao.findById(id);
        if (organization == null) {
            throw new ResourceNotFoundException("未找到指定ID的机构: " + id);
        }

        if (updateOrganizationDTO.getName() != null && !updateOrganizationDTO.getName().equals(organization.getName())) {
            if (organizationDao.findByName(updateOrganizationDTO.getName()).isPresent()) {
                throw new DataIntegrityViolationException("同名机构已存在: " + updateOrganizationDTO.getName());
            }
        }
        
        organizationMapper.updateOrganizationFromDto(updateOrganizationDTO, organization);
        organizationDao.update(organization);
        Organization updatedOrganization = organizationDao.findById(id);
        return organizationMapper.toDto(updatedOrganization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        if (organizationDao.findById(id) == null) {
            throw new ResourceNotFoundException("未找到指定ID的机构，无法删除: " + id);
        }
        organizationDao.deleteById(id);
    }
}
