package com.example.pension.model.face;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 认证记录信息实体
 * 用于接收人脸识别认证推送数据
 */
@Data
public class RecInfo {
    
    /**
     * 人员唯一ID
     */
    private String customId;
    
    /**
     * 人员ID
     */
    private String personId;
    
    /**
     * 控制记录库ID（断点续传用）
     */
    @JsonProperty("RecordID")
    private Long recordID;
    
    /**
     * 认证结果：1=允许，2=拒绝，22=待核验
     */
    @JsonProperty("VerifyStatus")
    private Integer verifyStatus;
    
    /**
     * 人员类型：0=白名单，1=黑名单
     */
    @JsonProperty("PersonType")
    private Integer personType;
    
    /**
     * 人脸相似度1
     */
    private Double similarity1;
    
    /**
     * 人脸相似度2
     */
    private Double similarity2;
    
    /**
     * 发送时间标识
     */
    @JsonProperty("Sendintime")
    private Integer sendintime;
    
    /**
     * 通行方向：enter=进入，exit=退出
     */
    private String direction;
    
    /**
     * 操作类型
     */
    private String otype;
    
    /**
     * 人员姓名（拼写错误版本，保持兼容）
     */
    private String persionName;
    
    /**
     * 人员姓名（正确拼写）
     */
    private String personName;
    
    /**
     * 闸机设备ID
     */
    private String facesluiceId;
    
    /**
     * 闸机设备名称
     */
    private String facesluiceName;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 电话号码
     */
    private String telnum;
    
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
     * 识别时间
     */
    private String time;
    
    /**
     * 推送类型
     */
    @JsonProperty("PushType")
    private String pushType;
    
    /**
     * 开门方式
     */
    @JsonProperty("OpendoorWay")
    private String opendoorWay;
    
    /**
     * 卡号2
     */
    private String cardNum2;
    
    /**
     * RFID卡号
     */
    @JsonProperty("RFIDCard")
    private String rfidCard;
    
    /**
     * 二维码数据
     */
    private String szQrCodeData;
    
    /**
     * 是否戴口罩：0=戴口罩，1=未戴口罩
     */
    private String isNoMask;
    
    /**
     * 文件索引
     */
    private String dwFileIndex;
    
    /**
     * 文件位置
     */
    private String dwFilePos;
    
    /**
     * 抓拍图片Base64编码
     */
    private String pic;
    
    /**
     * 体温（若支持）
     */
    private Double temperature;
    
    /**
     * 设备ID（兼容字段）
     */
    private String deviceId;
    
    /**
     * 抓拍时间（兼容字段）
     */
    private String captureTime;
    
    public String getCustomId() {
        return customId;
    }
    
    public void setCustomId(String customId) {
        this.customId = customId;
    }
    
    public Long getRecordID() {
        return recordID;
    }
    
    public void setRecordID(Long recordID) {
        this.recordID = recordID;
    }
    
    public Integer getVerifyStatus() {
        return verifyStatus;
    }
    
    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }
    
    public Double getSimilarity1() {
        return similarity1;
    }
    
    public void setSimilarity1(Double similarity1) {
        this.similarity1 = similarity1;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
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
    
    public String getPic() {
        return pic;
    }
    
    public void setPic(String pic) {
        this.pic = pic;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getCaptureTime() {
        return captureTime;
    }
    
    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }
}