package com.example.pension.model.face;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 断点续传响应实体
 * 用于向人脸识别一体机返回确认响应
 */
@Data
@NoArgsConstructor
public class Response {
    
    /**
     * 响应码：200表示成功
     */
    private Integer code;
    
    /**
     * 响应描述
     */
    private String desc;
    
    /**
     * 带参构造器
     */
    public Response(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}