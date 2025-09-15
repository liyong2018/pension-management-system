package com.example.pension.service;

import com.example.pension.model.onenet.WatchData;

/**
 * 手环数据服务接口
 */
public interface WatchDataService {
    
    /**
     * 处理接收到的手环数据
     * 
     * @param deviceId 设备ID
     * @param watchData 手环数据
     * @param timestamp 数据时间戳
     * @return 处理结果
     */
    boolean processWatchData(String deviceId, WatchData watchData, Long timestamp);
}