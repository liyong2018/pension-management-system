package com.example.pension.service.impl;

import com.example.pension.dao.WatchDataRecordDao;
import com.example.pension.model.WatchDataRecord;
import com.example.pension.model.onenet.WatchData;
import com.example.pension.service.WatchDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 手环数据服务实现类
 */
@Service
public class WatchDataServiceImpl implements WatchDataService {

    private static final Logger logger = LoggerFactory.getLogger(WatchDataServiceImpl.class);
    
    @Autowired
    private WatchDataRecordDao watchDataRecordDao;
    
    // 心率异常阈值
    private static final int HEART_RATE_LOW_THRESHOLD = 50;
    private static final int HEART_RATE_HIGH_THRESHOLD = 100;
    
    // 血压异常阈值（收缩压/舒张压）
    private static final int SYSTOLIC_PRESSURE_LOW_THRESHOLD = 90;
    private static final int SYSTOLIC_PRESSURE_HIGH_THRESHOLD = 140;
    private static final int DIASTOLIC_PRESSURE_LOW_THRESHOLD = 60;
    private static final int DIASTOLIC_PRESSURE_HIGH_THRESHOLD = 90;
    
    // 血氧饱和度异常阈值
    private static final int BLOOD_OXYGEN_LOW_THRESHOLD = 95;
    
    // 体温异常阈值
    private static final float BODY_TEMPERATURE_LOW_THRESHOLD = 36.0f;
    private static final float BODY_TEMPERATURE_HIGH_THRESHOLD = 37.5f;

    @Override
    public boolean processWatchData(String deviceId, WatchData watchData, Long timestamp) {
        if (watchData == null) {
            logger.warn("接收到空的手环数据，设备ID: {}", deviceId);
            return false;
        }
        
        logger.info("处理手环数据，设备ID: {}, 时间戳: {}", deviceId, timestamp);
        
        try {
            // 1. 检查生命体征数据是否异常
            checkVitalSigns(deviceId, watchData);
            
            // 2. 记录位置信息
            recordLocation(deviceId, watchData);
            
            // 3. 记录设备状态
            recordDeviceStatus(deviceId, watchData);
            
            // 4. 保存数据到数据库
            // TODO: 实现数据库存储逻辑
            saveToDatabase(deviceId, watchData, timestamp);
            
            return true;
        } catch (Exception e) {
            logger.error("处理手环数据异常，设备ID: {}", deviceId, e);
            return false;
        }
    }
    
    /**
     * 检查生命体征数据是否异常
     */
    private void checkVitalSigns(String deviceId, WatchData watchData) {
        // 检查心率
        if (watchData.getHeartRate() != null) {
            int heartRate = watchData.getHeartRate();
            if (heartRate < HEART_RATE_LOW_THRESHOLD) {
                logger.warn("设备 {} 心率过低: {} bpm", deviceId, heartRate);
                // TODO: 触发低心率告警
            } else if (heartRate > HEART_RATE_HIGH_THRESHOLD) {
                logger.warn("设备 {} 心率过高: {} bpm", deviceId, heartRate);
                // TODO: 触发高心率告警
            }
        }
        
        // 检查血压
        if (watchData.getSystolicPressure() != null && watchData.getDiastolicPressure() != null) {
            int systolic = watchData.getSystolicPressure();
            int diastolic = watchData.getDiastolicPressure();
            
            if (systolic < SYSTOLIC_PRESSURE_LOW_THRESHOLD || systolic > SYSTOLIC_PRESSURE_HIGH_THRESHOLD ||
                diastolic < DIASTOLIC_PRESSURE_LOW_THRESHOLD || diastolic > DIASTOLIC_PRESSURE_HIGH_THRESHOLD) {
                logger.warn("设备 {} 血压异常: {}/{} mmHg", deviceId, systolic, diastolic);
                // TODO: 触发血压异常告警
            }
        }
        
        // 检查血氧
        if (watchData.getBloodOxygen() != null) {
            int bloodOxygen = watchData.getBloodOxygen();
            if (bloodOxygen < BLOOD_OXYGEN_LOW_THRESHOLD) {
                logger.warn("设备 {} 血氧饱和度过低: {}%", deviceId, bloodOxygen);
                // TODO: 触发血氧过低告警
            }
        }
        
        // 检查体温
        if (watchData.getBodyTemperature() != null) {
            float bodyTemperature = watchData.getBodyTemperature();
            if (bodyTemperature < BODY_TEMPERATURE_LOW_THRESHOLD) {
                logger.warn("设备 {} 体温过低: {}°C", deviceId, bodyTemperature);
                // TODO: 触发体温过低告警
            } else if (bodyTemperature > BODY_TEMPERATURE_HIGH_THRESHOLD) {
                logger.warn("设备 {} 体温过高: {}°C", deviceId, bodyTemperature);
                // TODO: 触发体温过高告警
            }
        }
    }
    
    /**
     * 记录位置信息
     */
    private void recordLocation(String deviceId, WatchData watchData) {
        if (watchData.getLongitude() != null && watchData.getLatitude() != null) {
            double longitude = watchData.getLongitude();
            double latitude = watchData.getLatitude();
            logger.info("设备 {} 位置: 经度 {}, 纬度 {}", deviceId, longitude, latitude);
            // TODO: 更新设备位置信息
        }
    }
    
    /**
     * 记录设备状态
     */
    private void recordDeviceStatus(String deviceId, WatchData watchData) {
        // 记录电池电量
        if (watchData.getBatteryLevel() != null) {
            int batteryLevel = watchData.getBatteryLevel();
            logger.info("设备 {} 电量: {}%", deviceId, batteryLevel);
            
            if (batteryLevel < 20) {
                logger.warn("设备 {} 电量低: {}%", deviceId, batteryLevel);
                // TODO: 触发低电量告警
            }
        }
        
        // 记录充电状态
        if (watchData.getCharging() != null) {
            boolean charging = watchData.getCharging();
            logger.info("设备 {} 充电状态: {}", deviceId, charging ? "充电中" : "未充电");
        }
        
        // 记录设备状态
        if (watchData.getDeviceStatus() != null) {
            String deviceStatus = watchData.getDeviceStatus();
            logger.info("设备 {} 状态: {}", deviceId, deviceStatus);
        }
    }
    
    /**
     * 保存数据到数据库
     */
    private void saveToDatabase(String deviceId, WatchData watchData, Long timestamp) {
        try {
            // 创建数据记录对象
            WatchDataRecord record = new WatchDataRecord();
            record.setDeviceId(deviceId);
            record.setTimestamp(timestamp);
            
            // 设置生命体征数据
            record.setHeartRate(watchData.getHeartRate());
            record.setSystolicPressure(watchData.getSystolicPressure());
            record.setDiastolicPressure(watchData.getDiastolicPressure());
            record.setBloodOxygen(watchData.getBloodOxygen());
            record.setBodyTemperature(watchData.getBodyTemperature());
            
            // 设置活动数据
            record.setSteps(watchData.getSteps());
            record.setSleepStatus(watchData.getSleepStatus());
            
            // 设置位置数据
            record.setLongitude(watchData.getLongitude());
            record.setLatitude(watchData.getLatitude());
            
            // 设置设备状态
            record.setBatteryLevel(watchData.getBatteryLevel());
            record.setCharging(watchData.getCharging());
            record.setDeviceStatus(watchData.getDeviceStatus());
            
            // TODO: 根据设备ID查询关联的老人ID
            // 这里需要查询设备与老人的关联关系，暂时设为null
            record.setElderlyId(null);
            
            // 保存到数据库
            int result = watchDataRecordDao.insert(record);
            if (result > 0) {
                logger.info("成功保存设备 {} 的数据到数据库，记录ID: {}", deviceId, record.getId());
            } else {
                logger.warn("保存设备 {} 的数据到数据库失败", deviceId);
            }
        } catch (Exception e) {
            logger.error("保存设备 {} 的数据到数据库异常", deviceId, e);
        }
    }
}