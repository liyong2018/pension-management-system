package com.example.pension.mapper;

import com.example.pension.dto.RoleDTO;
import com.example.pension.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleDTOMapper {

    RoleDTOMapper INSTANCE = Mappers.getMapper(RoleDTOMapper.class);

    @Mapping(target = "permissionIds", ignore = true) // permissionIds需要单独处理
    RoleDTO toDTO(Role role);

    @Mapping(target = "permissions", ignore = true) // permissions需要单独处理
    Role toEntity(RoleDTO roleDTO);

    List<RoleDTO> toDTO(List<Role> roles);

    List<Role> toEntity(List<RoleDTO> roleDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    void updateEntityFromDTO(RoleDTO roleDTO, @MappingTarget Role role);
} 