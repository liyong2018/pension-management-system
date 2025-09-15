package com.example.pension.model.onenet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 手环数据实体
 * 用于接收OneNET平台推送的手环数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WatchData {
    
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
    
    // 手动添加getter和setter方法
    public Integer getHeartRate() {
        return heartRate;
    }
    
    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }
    
    public Integer getSystolicPressure() {
        return systolicPressure;
    }
    
    public void setSystolicPressure(Integer systolicPressure) {
        this.systolicPressure = systolicPressure;
    }
    
    public Integer getDiastolicPressure() {
        return diastolicPressure;
    }
    
    public void setDiastolicPressure(Integer diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }
    
    public Integer getBloodOxygen() {
        return bloodOxygen;
    }
    
    public void setBloodOxygen(Integer bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }
    
    public Float getBodyTemperature() {
        return bodyTemperature;
    }
    
    public void setBodyTemperature(Float bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }
    
    public Integer getSteps() {
        return steps;
    }
    
    public void setSteps(Integer steps) {
        this.steps = steps;
    }
    
    public Integer getSleepStatus() {
        return sleepStatus;
    }
    
    public void setSleepStatus(Integer sleepStatus) {
        this.sleepStatus = sleepStatus;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Integer getBatteryLevel() {
        return batteryLevel;
    }
    
    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public Boolean getCharging() {
        return charging;
    }
    
    public void setCharging(Boolean charging) {
        this.charging = charging;
    }
    
    public String getDeviceStatus() {
        return deviceStatus;
    }
    
    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}