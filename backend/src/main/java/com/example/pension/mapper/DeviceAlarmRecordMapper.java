package com.example.pension.mapper;

import com.example.pension.dto.DeviceAlarmRecordDTO;
import com.example.pension.model.DeviceAlarmRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceAlarmRecordMapper {

    DeviceAlarmRecordMapper INSTANCE = Mappers.getMapper(DeviceAlarmRecordMapper.class);

    @Mapping(source = "smartDevice.deviceName", target = "deviceName")
    @Mapping(source = "smartDevice.deviceCode", target = "deviceCode")
    DeviceAlarmRecordDTO toDTO(DeviceAlarmRecord deviceAlarmRecord);

    @Mapping(target = "smartDevice", ignore = true)
    DeviceAlarmRecord toEntity(DeviceAlarmRecordDTO deviceAlarmRecordDTO);

    List<DeviceAlarmRecordDTO> toDTO(List<DeviceAlarmRecord> deviceAlarmRecords);

    List<DeviceAlarmRecord> toEntity(List<DeviceAlarmRecordDTO> deviceAlarmRecordDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "smartDevice", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deviceId", ignore = true)
    void updateEntityFromDTO(DeviceAlarmRecordDTO deviceAlarmRecordDTO, @MappingTarget DeviceAlarmRecord deviceAlarmRecord);
} 