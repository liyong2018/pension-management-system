package com.example.pension.model.onenet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * OneNET平台推送请求实体
 * 用于接收OneNET平台推送的完整请求数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneNetPushRequest {
    
    /**
     * 推送的消息内容，JSON格式的设备数据
     */
    private String msg;
    
    /**
     * 平台生成的随机字符串
     */
    private String nonce;
    
    /**
     * 加密签名
     */
    private String signature;
    
    /**
     * 消息推送时间戳（毫秒）
     */
    private Long time;
    
    /**
     * 消息ID
     */
    private String id;
    
    // 手动添加getter和setter方法
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public String getNonce() {
        return nonce;
    }
    
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public Long getTime() {
        return time;
    }
    
    public void setTime(Long time) {
        this.time = time;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}