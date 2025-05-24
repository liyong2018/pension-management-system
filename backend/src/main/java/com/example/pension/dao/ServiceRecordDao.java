package com.example.pension.dao;

import com.example.pension.model.ServiceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ServiceRecordDao {
    
    ServiceRecord findById(@Param("id") Long id);

    List<ServiceRecord> findWithConditions(
            @Param("elderlyName") String elderlyName,
            @Param("serviceContent") String serviceContent,
            @Param("serviceProviderName") String serviceProviderName,
            @Param("serviceProviderType") String serviceProviderType,
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countWithConditions(
            @Param("elderlyName") String elderlyName,
            @Param("serviceContent") String serviceContent,
            @Param("serviceProviderName") String serviceProviderName,
            @Param("serviceProviderType") String serviceProviderType,
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    int insert(ServiceRecord serviceRecord);

    int update(ServiceRecord serviceRecord);

    int deleteById(@Param("id") Long id);

    List<ServiceRecord> findAllByElderlyId(@Param("elderlyId") Long elderlyId);

    // For statistics
    long countByStatus(@Param("status") String status);
    long countByServiceProviderType(@Param("serviceProviderType") String serviceProviderType);

    // For batch delete
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    // For evaluation
    List<ServiceRecord> findByStatusForEvaluation(@Param("status") String status);
} 