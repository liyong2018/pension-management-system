package com.example.pension.model.face;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通用推送数据实体
 * 用于接收人脸识别一体机的推送数据
 */
@Data
public class PushData<T> {
    
    /**
     * 推送类型：RecPush（认证记录）/StrSnapPush（陌生人抓拍）
     */
    private String operator;
    
    /**
     * 具体数据（动态类型）
     */
    private T info;
    
    // 手动添加getter方法以解决编译问题
    public String getOperator() {
        return operator;
    }
    
    public T getInfo() {
        return info;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public void setInfo(T info) {
        this.info = info;
    }
}