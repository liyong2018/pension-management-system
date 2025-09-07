package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceAlarmRecord {

    private Long id;

    private Long deviceId;

    private String alarmType;

    private String alarmLevel;

    private String alarmContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    private String alarmData;

    // 处理信息
    private String processStatus = "未处理";

    private String processPerson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    private String processResult;

    private String remarks;

    // 关联对象
    private SmartDevice smartDevice;

    // 系统字段
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getAlarmType() {
        return alarmType;
    }
    
    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }
    
    public String getAlarmLevel() {
        return alarmLevel;
    }
    
    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }
    
    public String getAlarmContent() {
        return alarmContent;
    }
    
    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }
    
    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }
    
    public void setAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
    }
    
    public String getAlarmData() {
        return alarmData;
    }
    
    public void setAlarmData(String alarmData) {
        this.alarmData = alarmData;
    }
    
    public String getProcessStatus() {
        return processStatus;
    }
    
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
    
    public String getProcessPerson() {
        return processPerson;
    }
    
    public void setProcessPerson(String processPerson) {
        this.processPerson = processPerson;
    }
    
    public LocalDateTime getProcessTime() {
        return processTime;
    }
    
    public void setProcessTime(LocalDateTime processTime) {
        this.processTime = processTime;
    }
    
    public String getProcessResult() {
        return processResult;
    }
    
    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public SmartDevice getSmartDevice() {
        return smartDevice;
    }
    
    public void setSmartDevice(SmartDevice smartDevice) {
        this.smartDevice = smartDevice;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}