package com.example.pension.service;

import com.example.pension.dto.DeviceAlarmRecordDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface DeviceAlarmRecordService {

    DeviceAlarmRecordDTO create(DeviceAlarmRecordDTO deviceAlarmRecordDTO);

    DeviceAlarmRecordDTO getById(Long id);

    PageInfo<DeviceAlarmRecordDTO> getAll(int pageNum, int pageSize);

    PageInfo<DeviceAlarmRecordDTO> searchByMultipleConditions(
            Long deviceId,
            String alarmType,
            String alarmLevel,
            String processStatus,
            int pageNum,
            int pageSize);

    DeviceAlarmRecordDTO update(Long id, DeviceAlarmRecordDTO deviceAlarmRecordDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    List<DeviceAlarmRecordDTO> getByDeviceId(Long deviceId);

    Map<String, Long> getAlarmTypeStatistics();

    Map<String, Long> getAlarmLevelStatistics();

    Map<String, Long> getProcessStatusStatistics();

    Long getUnprocessedAlarmCount();

    void processAlarm(Long id, String processPerson, String processResult, String remarks);
} 