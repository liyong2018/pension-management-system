package com.example.pension.dto;

public class DeviceTypeDetailedStatDTO {
    private String deviceType;
    private long totalCount;
    private long onlineCount;
    // private long faultCount; // 如果也需要故障数

    public DeviceTypeDetailedStatDTO() {
    }

    public DeviceTypeDetailedStatDTO(String deviceType, long totalCount, long onlineCount) {
        this.deviceType = deviceType;
        this.totalCount = totalCount;
        this.onlineCount = onlineCount;
    }

    // Getters and Setters
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }
} 