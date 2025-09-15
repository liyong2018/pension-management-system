package com.example.pension.model.onenet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * OneNET平台推送数据实体
 * 用于接收OneNET平台推送的手环数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneNetPushData<T> {
    
    /**
     * 消息ID
     */
    private String msgId;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    /**
     * 消息类型
     */
    private String msgType;
    
    /**
     * 具体数据（动态类型）
     */
    private T data;
    
    // 手动添加getter和setter方法
    public String getMsgId() {
        return msgId;
    }
    
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMsgType() {
        return msgType;
    }
    
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}