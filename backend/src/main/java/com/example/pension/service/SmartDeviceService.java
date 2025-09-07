package com.example.pension.service;

import com.example.pension.dto.SmartDeviceDTO;
import com.example.pension.dto.DeviceTypeDetailedStatDTO;
import com.example.pension.model.face.PushData;
import com.example.pension.model.face.HeartBeatInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface SmartDeviceService {

    SmartDeviceDTO create(SmartDeviceDTO smartDeviceDTO);

    SmartDeviceDTO getById(Long id);

    PageInfo<SmartDeviceDTO> getAll(int pageNum, int pageSize);

    PageInfo<SmartDeviceDTO> searchByMultipleConditions(
            String deviceName,
            String deviceType,
            String deviceStatus,
            Long elderlyId,
            Long organizationId,
            int pageNum,
            int pageSize);

    SmartDeviceDTO update(Long id, SmartDeviceDTO smartDeviceDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    List<SmartDeviceDTO> getByElderlyId(Long elderlyId);

    List<SmartDeviceDTO> getByOrganizationId(Long organizationId);

    Map<String, Long> getDeviceTypeStatistics();

    Map<String, Long> getDeviceStatusStatistics();

    List<SmartDeviceDTO> getDevicesNeedMaintenance();

    void updateDeviceStatus(Long deviceId, String status);

    void updateDeviceBatteryLevel(Long deviceId, Integer batteryLevel);

    void updateDeviceRealTimeData(Long deviceId, String realTimeData);

    List<DeviceTypeDetailedStatDTO> getDeviceDetailedStatistics();

    void updateLastCommunicationTime(String ipAddress);

    void updateLastCommunicationTimeByDeviceCode(String deviceCode);

    void updateLastCommunicationTimeWithHeartbeat(String ipAddress, PushData<HeartBeatInfo> heartbeatData);
    
    void updateLastCommunicationTimeWithHeartbeatByFacesluiceId(String facesluiceId, String ipAddress, PushData<HeartBeatInfo> heartbeatData);

    void checkAndUpdateOfflineDevices();
}