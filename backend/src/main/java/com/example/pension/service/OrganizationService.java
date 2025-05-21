package com.example.pension.service;

import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.mapper.OrganizationMapper;
import com.example.pension.model.Organization;
import com.example.pension.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrganizationDTO> getAllOrganizations(String name, Pageable pageable) {
        Specification<Organization> spec = (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(name)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                          "%" + name.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
        return organizationRepository.findAll(spec, pageable).map(organizationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> getOrganizationById(Long id) {
        return organizationRepository.findById(id).map(organizationMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> getOrganizationByName(String name) {
        return organizationRepository.findByName(name).map(organizationMapper::toDto);
    }

    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationDTO createOrganizationDTO) {
        if (organizationRepository.findByName(createOrganizationDTO.getName()).isPresent()) {
            throw new DataIntegrityViolationException("同名机构已存在: " + createOrganizationDTO.getName());
        }
        Organization organization = organizationMapper.toEntity(createOrganizationDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        return organizationMapper.toDto(savedOrganization);
    }

    @Transactional
    public OrganizationDTO updateOrganization(Long id, UpdateOrganizationDTO updateOrganizationDTO) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定ID的机构: " + id));

        if (updateOrganizationDTO.getName() != null && !updateOrganizationDTO.getName().equals(organization.getName())) {
            if (organizationRepository.findByName(updateOrganizationDTO.getName()).isPresent()) {
                throw new DataIntegrityViolationException("同名机构已存在: " + updateOrganizationDTO.getName());
            }
        }
        
        organizationMapper.updateOrganizationFromDto(updateOrganizationDTO, organization);
        Organization updatedOrganization = organizationRepository.save(organization);
        return organizationMapper.toDto(updatedOrganization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("未找到指定ID的机构，无法删除: " + id);
        }
        organizationRepository.deleteById(id);
    }
}
