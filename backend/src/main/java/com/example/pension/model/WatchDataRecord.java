package com.example.pension.model;

import lombok.Data;

import java.util.Date;

/**
 * 手环数据记录实体
 * 用于存储手环数据到数据库
 */
@Data
public class WatchDataRecord {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 老人ID（关联老人档案）
     */
    private Long elderlyId;
    
    /**
     * 心率数据
     */
    private Integer heartRate;
    
    /**
     * 血压数据 - 收缩压
     */
    private Integer systolicPressure;
    
    /**
     * 血压数据 - 舒张压
     */
    private Integer diastolicPressure;
    
    /**
     * 血氧饱和度
     */
    private Integer bloodOxygen;
    
    /**
     * 体温数据
     */
    private Float bodyTemperature;
    
    /**
     * 步数
     */
    private Integer steps;
    
    /**
     * 睡眠状态
     * 0: 清醒, 1: 浅睡, 2: 深睡
     */
    private Integer sleepStatus;
    
    /**
     * 位置信息 - 经度
     */
    private Double longitude;
    
    /**
     * 位置信息 - 纬度
     */
    private Double latitude;
    
    /**
     * 设备电量
     */
    private Integer batteryLevel;
    
    /**
     * 是否处于充电状态
     */
    private Boolean charging;
    
    /**
     * 设备状态
     */
    private String deviceStatus;
    
    /**
     * 数据时间戳
     */
    private Long timestamp;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}