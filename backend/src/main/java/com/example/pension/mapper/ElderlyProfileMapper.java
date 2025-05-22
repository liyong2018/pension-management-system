package com.example.pension.mapper;

import com.example.pension.dto.ElderlyProfileDTO;
import com.example.pension.model.ElderlyProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ElderlyFamilyMemberMapper.class})
public interface ElderlyProfileMapper {
    
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    ElderlyProfileDTO toDTO(ElderlyProfile entity);
    
    List<ElderlyProfileDTO> toDTO(List<ElderlyProfile> entities);
    
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "familyMembers", ignore = true)
    ElderlyProfile toEntity(ElderlyProfileDTO dto);
    
    @AfterMapping
    default void setOrganizationReference(@MappingTarget ElderlyProfile entity, ElderlyProfileDTO dto) {
        // Organization将通过Service逻辑设置
    }
    
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "familyMembers", ignore = true)
    void updateEntityFromDTO(ElderlyProfileDTO dto, @MappingTarget ElderlyProfile entity);
} 