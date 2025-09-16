package com.example.pension.model.onenet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * OneNET平台属性通知数据实体
 * 用于接收OneNET平台推送的设备属性变化通知
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneNetPropertyNotification {
    
    /**
     * 通知类型
     */
    private String notifyType;
    
    /**
     * 产品ID
     */
    private String productId;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 数据内容
     */
    private NotificationData data;
    
    /**
     * 通知数据内容
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NotificationData {
        
        /**
         * 数据ID
         */
        private String id;
        
        /**
         * 参数列表，每个参数包含时间戳和值
         */
        private Map<String, PropertyValue> params;
    }
    
    /**
     * 属性值，包含时间戳和具体值
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PropertyValue {
        
        /**
         * 时间戳
         */
        private Long time;
        
        /**
         * 属性值
         */
        private Object value;
        
        /**
         * 获取字符串值
         */
        public String getStringValue() {
            return value != null ? value.toString() : null;
        }
        
        /**
         * 获取整数值
         */
        public Integer getIntegerValue() {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
        
        /**
         * 获取浮点数值
         */
        public Double getDoubleValue() {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }
}