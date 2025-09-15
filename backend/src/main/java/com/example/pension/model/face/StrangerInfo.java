package com.example.pension.model.face;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 陌生人抓拍信息实体
 * 用于接收陌生人抓拍推送数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrangerInfo {
    
    /**
     * 抓拍库ID（断点续传用）
     */
    @JsonProperty("SnapID")
    private String SnapID;
    
    /**
     * 抓拍时间
     */
    @JsonProperty("Time")
    private String time;
    
    /**
     * 抓拍图片Base64编码
     */
    private String pic;
    
    /**
     * 体温（若支持）
     */
    private Double temperature;
    
    /**
     * 设备ID
     */
    @JsonProperty("DeviceID")
    private String facesluiceId;
    
    /**
     * 设备名称
     */
    private String facesluiceName;
    
    /**
     * 方向（进入/离开）
     */
    private String direction;
    
    /**
     * 人脸框左边界
     */
    private String left;
    
    /**
     * 人脸框上边界
     */
    private String top;
    
    /**
     * 人脸框右边界
     */
    private String right;
    
    /**
     * 人脸框下边界
     */
    private String bottom;
    
    /**
     * 推送类型
     */
    @JsonProperty("PushType")
    private String PushType;
    
    /**
     * 是否戴口罩
     */
    private String isNoMask;
    
    // 手动添加getter方法以解决编译问题
    public String getSnapID() {
        return SnapID;
    }
    
    public String getTime() {
        return time;
    }
    
    public String getPic() {
        return pic;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public String getFacesluiceId() {
        return facesluiceId;
    }
    
    public void setSnapID(String snapID) {
        SnapID = snapID;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public void setPic(String pic) {
        this.pic = pic;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public void setFacesluiceId(String facesluiceId) {
        this.facesluiceId = facesluiceId;
    }

    public String getFacesluiceName() {
        return facesluiceName;
    }

    public void setFacesluiceName(String facesluiceName) {
        this.facesluiceName = facesluiceName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getPushType() {
        return PushType;
    }

    public void setPushType(String pushType) {
        this.PushType = pushType;
    }

    public String getIsNoMask() {
        return isNoMask;
    }

    public void setIsNoMask(String isNoMask) {
        this.isNoMask = isNoMask;
    }
}