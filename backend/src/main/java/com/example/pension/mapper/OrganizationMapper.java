package com.example.pension.mapper;

import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    OrganizationDTO toDto(Organization organization);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Organization toEntity(CreateOrganizationDTO createOrganizationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateOrganizationFromDto(UpdateOrganizationDTO updateOrganizationDTO, @MappingTarget Organization organization);
}