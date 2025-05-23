package com.example.pension.service.impl;

import com.example.pension.dao.SmartDeviceDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.SmartDeviceDTO;
import com.example.pension.mapper.SmartDeviceMapper;
import com.example.pension.model.SmartDevice;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.Organization;
import com.example.pension.service.SmartDeviceService;
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
public class SmartDeviceServiceImpl implements SmartDeviceService {

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
        smartDevice.setLastCommunicationTime(LocalDateTime.now());
        smartDevice.setUpdateTime(LocalDateTime.now());
        smartDeviceDao.update(smartDevice);
    }
} 