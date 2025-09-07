package com.example.pension.model.face;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 心跳信息实体
 * 用于接收人脸识别设备心跳推送数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeartBeatInfo {
    
    /**
     * 设备ID（新格式）
     */
    @JsonProperty("DeviceID")
    private Long deviceId;
    
    /**
     * 设备IP地址
     */
    @JsonProperty("Ip")
    private String ip;
    
    /**
     * 设备MAC地址
     */
    @JsonProperty("MacAddr")
    private String macAddr;
    
    /**
     * WiFi IP地址
     */
    @JsonProperty("WifiIp")
    private String wifiIp;
    
    /**
     * 心跳时间（新格式）
     */
    @JsonProperty("Time")
    private String time;
    
    /**
     * 人脸识别一体机ID（保持向后兼容）
     */
    private String facesluiceId;
    
    public String getFacesluiceId() {
        return facesluiceId;
    }
    
    public void setFacesluiceId(String facesluiceId) {
        this.facesluiceId = facesluiceId;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
}