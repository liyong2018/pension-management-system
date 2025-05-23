package com.example.pension.dao;

import com.example.pension.model.DeviceAlarmRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceAlarmRecordDao {

    void insert(DeviceAlarmRecord deviceAlarmRecord);

    void update(DeviceAlarmRecord deviceAlarmRecord);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    void deleteByDeviceId(@Param("deviceId") Long deviceId);

    DeviceAlarmRecord findById(@Param("id") Long id);

    List<DeviceAlarmRecord> findWithConditions(@Param("deviceId") Long deviceId,
                                             @Param("alarmType") String alarmType,
                                             @Param("alarmLevel") String alarmLevel,
                                             @Param("processStatus") String processStatus,
                                             @Param("offset") Integer offset,
                                             @Param("limit") Integer limit);

    List<DeviceAlarmRecord> findAllByDeviceId(@Param("deviceId") Long deviceId);

    Long countByAlarmType(@Param("alarmType") String alarmType);

    Long countByAlarmLevel(@Param("alarmLevel") String alarmLevel);

    Long countByProcessStatus(@Param("processStatus") String processStatus);

    Long countUnprocessedAlarms();
} 