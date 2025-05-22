package com.example.pension.service;

import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.mapper.OrganizationMapper;
import com.example.pension.model.Organization;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageInfo<OrganizationDTO> getAllOrganizations(int pageNum, int pageSize, String name) {
        // 注意：这里不再使用PageHelper，因为我们自己处理分页
        List<Organization> organizations = organizationDao.findWithConditions(name, pageNum - 1, pageSize);
        List<OrganizationDTO> dtoList = organizations.stream()
                .map(organizationMapper::toDto)
                .collect(Collectors.toList());
        
        // 手动创建PageInfo对象
        PageInfo<OrganizationDTO> pageInfo = new PageInfo<>(dtoList);
        pageInfo.setTotal(organizationDao.countWithConditions(name));
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> getOrganizationById(Long id) {
        Organization organization = organizationDao.findById(id);
        return Optional.ofNullable(organization).map(organizationMapper::toDto);
    }

    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationDTO createOrganizationDTO) {
        // 暂时移除重名检查，如果需要，可以基于 findWithConditions 实现或添加 findByName 方法
        // if (organizationDao.findByName(createOrganizationDTO.getName()).isPresent()) {
        //     throw new DataIntegrityViolationException("同名机构已存在: " + createOrganizationDTO.getName());
        // }
        Organization organization = organizationMapper.toEntity(createOrganizationDTO);
        organizationDao.insert(organization); // MyBatis 中 insert 通常返回影响的行数，id 会通过 useGeneratedKeys 回填
        // 无需再次查询，MapStruct 会将回填的 id 映射到 DTO
        return organizationMapper.toDto(organization);
    }

    @Transactional
    public OrganizationDTO updateOrganization(Long id, UpdateOrganizationDTO updateOrganizationDTO) {
        Organization organization = organizationDao.findById(id);
        if (organization == null) {
            throw new ResourceNotFoundException("未找到指定ID的机构: " + id);
        }

        // 暂时移除重名检查
        // if (updateOrganizationDTO.getName() != null && !updateOrganizationDTO.getName().equals(organization.getName())) {
        //     if (organizationDao.findByName(updateOrganizationDTO.getName()).isPresent()) {
        //         throw new DataIntegrityViolationException("同名机构已存在: " + updateOrganizationDTO.getName());
        //     }
        // }
        
        organizationMapper.updateOrganizationFromDto(updateOrganizationDTO, organization);
        organizationDao.update(organization);
        // 更新后直接返回映射后的DTO，无需再次查询
        return organizationMapper.toDto(organization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        if (organizationDao.findById(id) == null) {
            throw new ResourceNotFoundException("未找到指定ID的机构，无法删除: " + id);
        }
        organizationDao.deleteById(id);
    }

    @Transactional
    public void deleteOrganizations(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 或者抛出异常，视业务需求而定
        }
        // 批量删除前可以添加一些检查，例如检查这些ID是否存在
        organizationDao.deleteBatchByIds(ids);
    }
}
