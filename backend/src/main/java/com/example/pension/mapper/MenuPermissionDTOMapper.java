package com.example.pension.mapper;

import com.example.pension.dto.MenuPermissionDTO;
import com.example.pension.model.MenuPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MenuPermissionDTOMapper {

    MenuPermissionDTOMapper INSTANCE = Mappers.getMapper(MenuPermissionDTOMapper.class);

    MenuPermissionDTO toDTO(MenuPermission menuPermission);

    MenuPermission toEntity(MenuPermissionDTO menuPermissionDTO);

    List<MenuPermissionDTO> toDTO(List<MenuPermission> menuPermissions);

    List<MenuPermission> toEntity(List<MenuPermissionDTO> menuPermissionDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    void updateEntityFromDTO(MenuPermissionDTO menuPermissionDTO, @MappingTarget MenuPermission menuPermission);
} 