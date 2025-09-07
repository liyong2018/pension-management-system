package com.example.pension.scheduler;

import com.example.pension.service.SmartDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 设备状态定时检查调度器
 * 用于定期检查设备心跳状态，将超过60秒无心跳的设备标记为离线
 */
@Component
public class DeviceStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusScheduler.class);

    @Autowired
    private SmartDeviceService smartDeviceService;

    /**
     * 每30秒执行一次设备状态检查
     * 检查所有在线设备，将超过60秒无心跳的设备标记为离线
     */
    @Scheduled(fixedRate = 30000) // 30秒执行一次
    public void checkDeviceStatus() {
        try {
            logger.debug("开始执行设备状态检查定时任务");
            smartDeviceService.checkAndUpdateOfflineDevices();
            logger.debug("设备状态检查定时任务执行完成");
        } catch (Exception e) {
            logger.error("设备状态检查定时任务执行失败", e);
        }
    }
}