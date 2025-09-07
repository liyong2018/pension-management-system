package com.example.pension.service.impl;

import com.example.pension.dao.SmartDeviceDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.SmartDeviceDTO;
import com.example.pension.dto.DeviceTypeDetailedStatDTO;
import com.example.pension.mapper.SmartDeviceMapper;
import com.example.pension.model.SmartDevice;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.Organization;
import com.example.pension.model.face.HeartBeatInfo;
import com.example.pension.model.face.PushData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.pension.service.SmartDeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SmartDeviceServiceImpl implements SmartDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(SmartDeviceServiceImpl.class);

    @Autowired
    private SmartDeviceDao smartDeviceDao;

    @Autowired
    private ElderlyProfileDao elderlyProfileDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public SmartDeviceDTO create(SmartDeviceDTO smartDeviceDTO) {
        // 验证设备编号是否重复
        SmartDevice existingDevice = smartDeviceDao.findByDeviceCode(smartDeviceDTO.getDeviceCode());
        if (existingDevice != null) {
            throw new RuntimeException("设备编号已存在：" + smartDeviceDTO.getDeviceCode());
        }

        SmartDevice smartDevice = SmartDeviceMapper.INSTANCE.toEntity(smartDeviceDTO);
        
        // 设置关联对象
        if (smartDeviceDTO.getElderlyId() != null) {
            ElderlyProfile elderlyProfile = elderlyProfileDao.findById(smartDeviceDTO.getElderlyId());
            if (elderlyProfile == null) {
                throw new RuntimeException("关联的老人不存在，ID：" + smartDeviceDTO.getElderlyId());
            }
            smartDevice.setElderlyProfile(elderlyProfile);
        }

        if (smartDeviceDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(smartDeviceDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + smartDeviceDTO.getOrganizationId());
            }
            smartDevice.setOrganization(organization);
        }

        // 设置系统字段
        smartDevice.setCreateTime(LocalDateTime.now());
        smartDevice.setUpdateTime(LocalDateTime.now());
        smartDevice.setIsDeleted(false);

        smartDeviceDao.insert(smartDevice);
        return getById(smartDevice.getId());
    }

    @Override
    public SmartDeviceDTO getById(Long id) {
        SmartDevice smartDevice = smartDeviceDao.findById(id);
        if (smartDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + id);
        }
        return SmartDeviceMapper.INSTANCE.toDTO(smartDevice);
    }

    @Override
    public PageInfo<SmartDeviceDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<SmartDevice> smartDevices = smartDeviceDao.findWithConditions(
                null, null, null, null, null, null, null);
        PageInfo<SmartDevice> pageInfo = new PageInfo<>(smartDevices);
        
        List<SmartDeviceDTO> smartDeviceDTOs = SmartDeviceMapper.INSTANCE.toDTO(smartDevices);
        
        PageInfo<SmartDeviceDTO> result = new PageInfo<>();
        result.setList(smartDeviceDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public PageInfo<SmartDeviceDTO> searchByMultipleConditions(
            String deviceName, String deviceType, String deviceStatus,
            Long elderlyId, Long organizationId, int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<SmartDevice> smartDevices = smartDeviceDao.findWithConditions(
                deviceName, deviceType, deviceStatus, elderlyId, organizationId, null, null);
        PageInfo<SmartDevice> pageInfo = new PageInfo<>(smartDevices);
        
        List<SmartDeviceDTO> smartDeviceDTOs = SmartDeviceMapper.INSTANCE.toDTO(smartDevices);
        
        PageInfo<SmartDeviceDTO> result = new PageInfo<>();
        result.setList(smartDeviceDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public SmartDeviceDTO update(Long id, SmartDeviceDTO smartDeviceDTO) {
        SmartDevice existingDevice = smartDeviceDao.findById(id);
        if (existingDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + id);
        }

        // 验证设备编号是否重复（排除当前设备）
        if (smartDeviceDTO.getDeviceCode() != null && 
            !smartDeviceDTO.getDeviceCode().equals(existingDevice.getDeviceCode())) {
            SmartDevice duplicateDevice = smartDeviceDao.findByDeviceCodeAndIdNot(
                    smartDeviceDTO.getDeviceCode(), id);
            if (duplicateDevice != null) {
                throw new RuntimeException("设备编号已存在：" + smartDeviceDTO.getDeviceCode());
            }
        }

        // 更新字段
        SmartDeviceMapper.INSTANCE.updateEntityFromDTO(smartDeviceDTO, existingDevice);
        
        // 设置关联对象
        if (smartDeviceDTO.getElderlyId() != null) {
            ElderlyProfile elderlyProfile = elderlyProfileDao.findById(smartDeviceDTO.getElderlyId());
            if (elderlyProfile == null) {
                throw new RuntimeException("关联的老人不存在，ID：" + smartDeviceDTO.getElderlyId());
            }
            existingDevice.setElderlyProfile(elderlyProfile);
        } else {
            existingDevice.setElderlyProfile(null);
        }

        if (smartDeviceDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(smartDeviceDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + smartDeviceDTO.getOrganizationId());
            }
            existingDevice.setOrganization(organization);
        } else {
            existingDevice.setOrganization(null);
        }

        existingDevice.setUpdateTime(LocalDateTime.now());
        smartDeviceDao.update(existingDevice);
        
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        SmartDevice smartDevice = smartDeviceDao.findById(id);
        if (smartDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + id);
        }
        smartDeviceDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除列表不能为空");
        }
        smartDeviceDao.deleteBatchByIds(ids);
    }

    @Override
    public List<SmartDeviceDTO> getByElderlyId(Long elderlyId) {
        List<SmartDevice> smartDevices = smartDeviceDao.findAllByElderlyId(elderlyId);
        return SmartDeviceMapper.INSTANCE.toDTO(smartDevices);
    }

    @Override
    public List<SmartDeviceDTO> getByOrganizationId(Long organizationId) {
        List<SmartDevice> smartDevices = smartDeviceDao.findAllByOrganizationId(organizationId);
        return SmartDeviceMapper.INSTANCE.toDTO(smartDevices);
    }

    @Override
    public Map<String, Long> getDeviceTypeStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("健康监测设备", smartDeviceDao.countByDeviceType("健康监测设备"));
        statistics.put("智能家居设备", smartDeviceDao.countByDeviceType("智能家居设备"));
        statistics.put("安全设备", smartDeviceDao.countByDeviceType("安全设备"));
        statistics.put("定位设备", smartDeviceDao.countByDeviceType("定位设备"));
        statistics.put("人脸识别设备", smartDeviceDao.countByDeviceType("人脸识别设备"));
        return statistics;
    }

    @Override
    public Map<String, Long> getDeviceStatusStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("在线", smartDeviceDao.countByDeviceStatus("在线"));
        statistics.put("离线", smartDeviceDao.countByDeviceStatus("离线"));
        statistics.put("故障", smartDeviceDao.countByDeviceStatus("故障"));
        statistics.put("维护中", smartDeviceDao.countByDeviceStatus("维护中"));
        return statistics;
    }

    @Override
    public List<SmartDeviceDTO> getDevicesNeedMaintenance() {
        List<SmartDevice> smartDevices = smartDeviceDao.findDevicesNeedMaintenance();
        return SmartDeviceMapper.INSTANCE.toDTO(smartDevices);
    }

    @Override
    public void updateDeviceStatus(Long deviceId, String status) {
        SmartDevice smartDevice = smartDeviceDao.findById(deviceId);
        if (smartDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + deviceId);
        }
        smartDevice.setDeviceStatus(status);
        smartDevice.setUpdateTime(LocalDateTime.now());
        smartDeviceDao.update(smartDevice);
    }

    @Override
    public void updateDeviceBatteryLevel(Long deviceId, Integer batteryLevel) {
        SmartDevice smartDevice = smartDeviceDao.findById(deviceId);
        if (smartDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + deviceId);
        }
        smartDevice.setBatteryLevel(batteryLevel);
        smartDevice.setUpdateTime(LocalDateTime.now());
        smartDeviceDao.update(smartDevice);
    }

    @Override
    public void updateDeviceRealTimeData(Long deviceId, String realTimeData) {
        SmartDevice smartDevice = smartDeviceDao.findById(deviceId);
        if (smartDevice == null) {
            throw new RuntimeException("设备不存在，ID：" + deviceId);
        }
        smartDevice.setRealTimeData(realTimeData);
        smartDevice.setUpdateTime(LocalDateTime.now());
        smartDeviceDao.update(smartDevice);
    }

    @Override
    public List<DeviceTypeDetailedStatDTO> getDeviceDetailedStatistics() {
        return smartDeviceDao.getDeviceStatisticsGroupedByType();
    }

    @Override
    public void updateLastCommunicationTime(String ipAddress) {
        SmartDevice device = smartDeviceDao.findByIpAddress(ipAddress);
        if (device != null) {
            device.setLastCommunicationTime(LocalDateTime.now());
            device.setDeviceStatus("在线");
            device.setUpdateTime(LocalDateTime.now());
            smartDeviceDao.update(device);
        }
    }

    @Override
    public void updateLastCommunicationTimeByDeviceCode(String deviceCode) {
        SmartDevice device = smartDeviceDao.findByDeviceCode(deviceCode);
        if (device != null) {
            device.setLastCommunicationTime(LocalDateTime.now());
            device.setDeviceStatus("在线");
            device.setUpdateTime(LocalDateTime.now());
            smartDeviceDao.update(device);
        }
    }

    @Override
    public void updateLastCommunicationTimeWithHeartbeat(String ipAddress, PushData<HeartBeatInfo> heartbeatData) {
        SmartDevice device = smartDeviceDao.findByIpAddress(ipAddress);
        if (device != null) {
            device.setLastCommunicationTime(LocalDateTime.now());
            device.setDeviceStatus("在线");
            device.setUpdateTime(LocalDateTime.now());
            
            // 将心跳数据转换为JSON格式存储到实时数据字段
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String heartbeatJson = objectMapper.writeValueAsString(heartbeatData);
                device.setRealTimeData(heartbeatJson);
                logger.info("设备 {} 心跳数据已存储: {}", ipAddress, heartbeatJson);
            } catch (Exception e) {
                logger.error("转换心跳数据为JSON失败: {}", e.getMessage(), e);
                // 即使JSON转换失败，也要更新通信时间
            }
            
            smartDeviceDao.update(device);
        } else {
            logger.warn("未找到IP地址为 {} 的设备", ipAddress);
        }
    }
    
    @Override
    public void updateLastCommunicationTimeWithHeartbeatByFacesluiceId(String facesluiceId, String ipAddress, PushData<HeartBeatInfo> heartbeatData) {
        SmartDevice device = null;
        HeartBeatInfo info = heartbeatData.getInfo();
        
        // 优先使用新格式的DeviceID进行设备匹配
        if (info != null && info.getDeviceId() != null) {
            String deviceIdStr = String.valueOf(info.getDeviceId());
            device = smartDeviceDao.findByDeviceCode(deviceIdStr);
            logger.info("尝试通过DeviceID={} 查找设备: {}", deviceIdStr, device != null ? "找到" : "未找到");
        }
        
        // 如果通过DeviceID找不到，尝试通过facesluiceId作为device_code查找设备（向后兼容）
        if (device == null && facesluiceId != null && !facesluiceId.isEmpty()) {
            device = smartDeviceDao.findByDeviceCode(facesluiceId);
            logger.info("尝试通过facesluiceId={} 查找设备: {}", facesluiceId, device != null ? "找到" : "未找到");
        }
        
        // 如果通过设备ID都找不到，则通过IP地址查找
        if (device == null && ipAddress != null && !ipAddress.isEmpty()) {
            device = smartDeviceDao.findByIpAddress(ipAddress);
            logger.info("尝试通过IP地址={} 查找设备: {}", ipAddress, device != null ? "找到" : "未找到");
        }
        
        if (device != null) {
            device.setLastCommunicationTime(LocalDateTime.now());
            device.setDeviceStatus("在线");
            device.setUpdateTime(LocalDateTime.now());
            
            // 将心跳数据转换为JSON格式存储到实时数据字段
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String heartbeatJson = objectMapper.writeValueAsString(heartbeatData);
                device.setRealTimeData(heartbeatJson);
                logger.info("设备 facesluiceId={} IP={} 心跳数据已存储: {}", facesluiceId, ipAddress, heartbeatJson);
            } catch (Exception e) {
                logger.error("转换心跳数据为JSON失败: {}", e.getMessage(), e);
                // 即使JSON转换失败，也要更新通信时间
            }
            
            smartDeviceDao.update(device);
            logger.info("已更新设备 device_code={} IP={} 的心跳信息", device.getDeviceCode(), device.getIpAddress());
        } else {
            logger.warn("未找到facesluiceId={} 或 IP地址={} 对应的设备", facesluiceId, ipAddress);
        }
    }

    @Override
    public void checkAndUpdateOfflineDevices() {
        // 查找所有在线设备
        List<SmartDevice> onlineDevices = smartDeviceDao.findWithConditions(
                null, null, "在线", null, null, null, null);
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(60);
        int updatedCount = 0;
        
        for (SmartDevice device : onlineDevices) {
            // 如果设备有最后通信时间且超过60秒没有心跳，标记为离线
            if (device.getLastCommunicationTime() != null && 
                device.getLastCommunicationTime().isBefore(cutoffTime)) {
                device.setDeviceStatus("离线");
                device.setUpdateTime(LocalDateTime.now());
                smartDeviceDao.update(device);
                updatedCount++;
                logger.info("设备 {} ({}) 超过60秒无心跳，已标记为离线", 
                           device.getDeviceName(), device.getIpAddress());
            }
        }
        
        if (updatedCount > 0) {
            logger.info("定时任务完成，共有 {} 个设备被标记为离线", updatedCount);
        }
    }
}