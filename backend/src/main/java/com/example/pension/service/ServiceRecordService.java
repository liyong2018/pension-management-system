package com.example.pension.service;

import com.example.pension.dto.ServiceRecordDTO;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ServiceRecordService {
    
    ServiceRecordDTO create(ServiceRecordDTO serviceRecordDTO);
    
    ServiceRecordDTO getById(Long id);
    
    PageInfo<ServiceRecordDTO> getAll(int pageNum, int pageSize);
    
    PageInfo<ServiceRecordDTO> searchByMultipleConditions(
            String elderlyName,
            String serviceContent,
            String serviceProviderName,
            String serviceProviderType,
            Long serviceProviderId,
            String status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int pageNum,
            int pageSize);
    
    ServiceRecordDTO update(Long id, ServiceRecordDTO serviceRecordDTO);
    
    void delete(Long id);
    
    void batchDelete(List<Long> ids);
    
    List<ServiceRecordDTO> getByElderlyId(Long elderlyId);
    
    Map<String, Long> getStatusStatistics();
    
    Map<String, Long> getServiceProviderTypeStatistics();
    
    List<ServiceRecordDTO> getRecordsForEvaluation();
    
    ServiceRecordDTO evaluate(Long id, Integer score, String comment);
} 