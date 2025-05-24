package com.example.pension.mapper;

import com.example.pension.dto.SystemUserDTO;
import com.example.pension.model.SystemUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SystemUserDTOMapper {

    SystemUserDTOMapper INSTANCE = Mappers.getMapper(SystemUserDTOMapper.class);

    @Mapping(target = "password", ignore = true) // 不映射密码字段
    @Mapping(target = "roleIds", ignore = true) // roleIds需要单独处理
    SystemUserDTO toDTO(SystemUser systemUser);

    @Mapping(target = "passwordHash", ignore = true) // passwordHash需要单独处理
    @Mapping(target = "roles", ignore = true) // roles需要单独处理
    SystemUser toEntity(SystemUserDTO systemUserDTO);

    List<SystemUserDTO> toDTO(List<SystemUser> systemUsers);

    List<SystemUser> toEntity(List<SystemUserDTO> systemUserDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "lastLoginTime", ignore = true)
    void updateEntityFromDTO(SystemUserDTO systemUserDTO, @MappingTarget SystemUser systemUser);
} 