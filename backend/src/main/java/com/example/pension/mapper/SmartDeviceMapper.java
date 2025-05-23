package com.example.pension.mapper;

import com.example.pension.dto.SmartDeviceDTO;
import com.example.pension.model.SmartDevice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SmartDeviceMapper {

    SmartDeviceMapper INSTANCE = Mappers.getMapper(SmartDeviceMapper.class);

    @Mapping(source = "elderlyProfile.id", target = "elderlyId")
    @Mapping(source = "elderlyProfile.name", target = "elderlyName")
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    SmartDeviceDTO toDTO(SmartDevice smartDevice);

    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "organization", ignore = true)
    SmartDevice toEntity(SmartDeviceDTO smartDeviceDTO);

    List<SmartDeviceDTO> toDTO(List<SmartDevice> smartDevices);

    List<SmartDevice> toEntity(List<SmartDeviceDTO> smartDeviceDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromDTO(SmartDeviceDTO smartDeviceDTO, @MappingTarget SmartDevice smartDevice);
} 