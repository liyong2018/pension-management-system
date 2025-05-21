package com.example.pension.mapper;

import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.model.Organization;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    OrganizationDTO toDto(Organization organization);

    Organization toEntity(CreateOrganizationDTO createOrganizationDTO);

    void updateOrganizationFromDto(UpdateOrganizationDTO updateOrganizationDTO, @MappingTarget Organization organization);
} 