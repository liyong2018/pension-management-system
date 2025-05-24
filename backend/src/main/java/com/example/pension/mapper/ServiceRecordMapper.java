package com.example.pension.mapper;

import com.example.pension.dto.ServiceRecordDTO;
import com.example.pension.model.ServiceRecord;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceRecordMapper {
    
    @Mapping(target = "elderlyName", source = "elderlyProfile.name")
    ServiceRecordDTO toDTO(ServiceRecord entity);
    
    List<ServiceRecordDTO> toDTO(List<ServiceRecord> entities);
    
    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "elderlyName", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    ServiceRecord toEntity(ServiceRecordDTO dto);
    
    @Mapping(target = "elderlyProfile", ignore = true)
    @Mapping(target = "elderlyName", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntityFromDTO(ServiceRecordDTO dto, @MappingTarget ServiceRecord entity);
} 