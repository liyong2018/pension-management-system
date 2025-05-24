package com.example.pension.mapper;

import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.model.ElderlyFamilyMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ElderlyFamilyMemberDTOMapper {
    
    @Mapping(target = "elderlyId", source = "elderlyProfile.id")
    ElderlyFamilyMemberDTO toDTO(ElderlyFamilyMember entity);
    
    List<ElderlyFamilyMemberDTO> toDTO(List<ElderlyFamilyMember> entities);
    
    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    ElderlyFamilyMember toEntity(ElderlyFamilyMemberDTO dto);
    
    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntityFromDTO(ElderlyFamilyMemberDTO dto, @MappingTarget ElderlyFamilyMember entity);
} 