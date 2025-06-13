package com.example.pension.dao;

import com.example.pension.model.SmartDevice;
import com.example.pension.dto.DeviceTypeDetailedStatDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmartDeviceDao {

    void insert(SmartDevice smartDevice);

    void update(SmartDevice smartDevice);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    SmartDevice findById(@Param("id") Long id);

    SmartDevice findByDeviceCode(@Param("deviceCode") String deviceCode);

    SmartDevice findByDeviceCodeAndIdNot(@Param("deviceCode") String deviceCode, @Param("id") Long id);

    List<SmartDevice> findWithConditions(@Param("deviceName") String deviceName,
                                       @Param("deviceType") String deviceType,
                                       @Param("deviceStatus") String deviceStatus,
                                       @Param("elderlyId") Long elderlyId,
                                       @Param("organizationId") Long organizationId,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    List<SmartDevice> findAllByElderlyId(@Param("elderlyId") Long elderlyId);

    List<SmartDevice> findAllByOrganizationId(@Param("organizationId") Long organizationId);

    Long countByDeviceType(@Param("deviceType") String deviceType);

    Long countByDeviceStatus(@Param("deviceStatus") String deviceStatus);

    Long countByElderlyId(@Param("elderlyId") Long elderlyId);

    List<SmartDevice> findDevicesNeedMaintenance();

    List<DeviceTypeDetailedStatDTO> getDeviceStatisticsGroupedByType();
} 