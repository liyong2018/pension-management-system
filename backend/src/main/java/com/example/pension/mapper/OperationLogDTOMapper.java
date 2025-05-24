package com.example.pension.mapper;

import com.example.pension.dto.OperationLogDTO;
import com.example.pension.model.OperationLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationLogDTOMapper {
    
    OperationLogDTOMapper INSTANCE = Mappers.getMapper(OperationLogDTOMapper.class);
    
    OperationLogDTO toDTO(OperationLog operationLog);
    
    List<OperationLogDTO> toDTOList(List<OperationLog> operationLogs);
    
    OperationLog toEntity(OperationLogDTO operationLogDTO);
} 