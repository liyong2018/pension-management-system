package com.example.pension.service.impl;

import com.example.pension.dao.DeviceAlarmRecordDao;
import com.example.pension.dao.SmartDeviceDao;
import com.example.pension.dto.DeviceAlarmRecordDTO;
import com.example.pension.mapper.DeviceAlarmRecordMapper;
import com.example.pension.model.DeviceAlarmRecord;
import com.example.pension.model.SmartDevice;
import com.example.pension.service.DeviceAlarmRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeviceAlarmRecordServiceImpl implements DeviceAlarmRecordService {

    @Autowired
    private DeviceAlarmRecordDao deviceAlarmRecordDao;

    @Autowired
    private SmartDeviceDao smartDeviceDao;

    @Override
    public DeviceAlarmRecordDTO create(DeviceAlarmRecordDTO deviceAlarmRecordDTO) {
        // 验证设备是否存在
        SmartDevice smartDevice = smartDeviceDao.findById(deviceAlarmRecordDTO.getDeviceId());
        if (smartDevice == null) {
            throw new RuntimeException("关联的设备不存在，ID：" + deviceAlarmRecordDTO.getDeviceId());
        }

        DeviceAlarmRecord deviceAlarmRecord = DeviceAlarmRecordMapper.INSTANCE.toEntity(deviceAlarmRecordDTO);
        deviceAlarmRecord.setSmartDevice(smartDevice);
        deviceAlarmRecord.setCreateTime(LocalDateTime.now());
        deviceAlarmRecord.setUpdateTime(LocalDateTime.now());

        deviceAlarmRecordDao.insert(deviceAlarmRecord);
        return getById(deviceAlarmRecord.getId());
    }

    @Override
    public DeviceAlarmRecordDTO getById(Long id) {
        DeviceAlarmRecord deviceAlarmRecord = deviceAlarmRecordDao.findById(id);
        if (deviceAlarmRecord == null) {
            throw new RuntimeException("告警记录不存在，ID：" + id);
        }
        return DeviceAlarmRecordMapper.INSTANCE.toDTO(deviceAlarmRecord);
    }

    @Override
    public PageInfo<DeviceAlarmRecordDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "alarm_time desc");
        List<DeviceAlarmRecord> deviceAlarmRecords = deviceAlarmRecordDao.findWithConditions(
                null, null, null, null, null, null);
        PageInfo<DeviceAlarmRecord> pageInfo = new PageInfo<>(deviceAlarmRecords);
        
        List<DeviceAlarmRecordDTO> deviceAlarmRecordDTOs = DeviceAlarmRecordMapper.INSTANCE.toDTO(deviceAlarmRecords);
        
        PageInfo<DeviceAlarmRecordDTO> result = new PageInfo<>();
        result.setList(deviceAlarmRecordDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public PageInfo<DeviceAlarmRecordDTO> searchByMultipleConditions(
            Long deviceId, String alarmType, String alarmLevel, String processStatus,
            int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize, "alarm_time desc");
        List<DeviceAlarmRecord> deviceAlarmRecords = deviceAlarmRecordDao.findWithConditions(
                deviceId, alarmType, alarmLevel, processStatus, null, null);
        PageInfo<DeviceAlarmRecord> pageInfo = new PageInfo<>(deviceAlarmRecords);
        
        List<DeviceAlarmRecordDTO> deviceAlarmRecordDTOs = DeviceAlarmRecordMapper.INSTANCE.toDTO(deviceAlarmRecords);
        
        PageInfo<DeviceAlarmRecordDTO> result = new PageInfo<>();
        result.setList(deviceAlarmRecordDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public DeviceAlarmRecordDTO update(Long id, DeviceAlarmRecordDTO deviceAlarmRecordDTO) {
        DeviceAlarmRecord existingRecord = deviceAlarmRecordDao.findById(id);
        if (existingRecord == null) {
            throw new RuntimeException("告警记录不存在，ID：" + id);
        }

        // 验证设备是否存在
        if (deviceAlarmRecordDTO.getDeviceId() != null) {
            SmartDevice smartDevice = smartDeviceDao.findById(deviceAlarmRecordDTO.getDeviceId());
            if (smartDevice == null) {
                throw new RuntimeException("关联的设备不存在，ID：" + deviceAlarmRecordDTO.getDeviceId());
            }
            existingRecord.setSmartDevice(smartDevice);
        }

        // 更新字段
        DeviceAlarmRecordMapper.INSTANCE.updateEntityFromDTO(deviceAlarmRecordDTO, existingRecord);
        existingRecord.setUpdateTime(LocalDateTime.now());
        
        deviceAlarmRecordDao.update(existingRecord);
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        DeviceAlarmRecord deviceAlarmRecord = deviceAlarmRecordDao.findById(id);
        if (deviceAlarmRecord == null) {
            throw new RuntimeException("告警记录不存在，ID：" + id);
        }
        deviceAlarmRecordDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除列表不能为空");
        }
        deviceAlarmRecordDao.deleteBatchByIds(ids);
    }

    @Override
    public List<DeviceAlarmRecordDTO> getByDeviceId(Long deviceId) {
        List<DeviceAlarmRecord> deviceAlarmRecords = deviceAlarmRecordDao.findAllByDeviceId(deviceId);
        return DeviceAlarmRecordMapper.INSTANCE.toDTO(deviceAlarmRecords);
    }

    @Override
    public Map<String, Long> getAlarmTypeStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("设备故障", deviceAlarmRecordDao.countByAlarmType("设备故障"));
        statistics.put("数据异常", deviceAlarmRecordDao.countByAlarmType("数据异常"));
        statistics.put("低电量", deviceAlarmRecordDao.countByAlarmType("低电量"));
        statistics.put("网络断连", deviceAlarmRecordDao.countByAlarmType("网络断连"));
        statistics.put("健康异常", deviceAlarmRecordDao.countByAlarmType("健康异常"));
        return statistics;
    }

    @Override
    public Map<String, Long> getAlarmLevelStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("严重", deviceAlarmRecordDao.countByAlarmLevel("严重"));
        statistics.put("警告", deviceAlarmRecordDao.countByAlarmLevel("警告"));
        statistics.put("提醒", deviceAlarmRecordDao.countByAlarmLevel("提醒"));
        return statistics;
    }

    @Override
    public Map<String, Long> getProcessStatusStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("未处理", deviceAlarmRecordDao.countByProcessStatus("未处理"));
        statistics.put("处理中", deviceAlarmRecordDao.countByProcessStatus("处理中"));
        statistics.put("已处理", deviceAlarmRecordDao.countByProcessStatus("已处理"));
        statistics.put("已忽略", deviceAlarmRecordDao.countByProcessStatus("已忽略"));
        return statistics;
    }

    @Override
    public Long getUnprocessedAlarmCount() {
        return deviceAlarmRecordDao.countUnprocessedAlarms();
    }

    @Override
    public void processAlarm(Long id, String processPerson, String processResult, String remarks) {
        DeviceAlarmRecord deviceAlarmRecord = deviceAlarmRecordDao.findById(id);
        if (deviceAlarmRecord == null) {
            throw new RuntimeException("告警记录不存在，ID：" + id);
        }
        
        deviceAlarmRecord.setProcessStatus("已处理");
        deviceAlarmRecord.setProcessPerson(processPerson);
        deviceAlarmRecord.setProcessTime(LocalDateTime.now());
        deviceAlarmRecord.setProcessResult(processResult);
        deviceAlarmRecord.setRemarks(remarks);
        deviceAlarmRecord.setUpdateTime(LocalDateTime.now());
        
        deviceAlarmRecordDao.update(deviceAlarmRecord);
    }
} 