package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人脸识别记录实体类
 * 用于存储人脸识别认证记录
 */
@Data
public class FaceRecognitionRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 人员唯一ID
     */
    private String customId;
    
    /**
     * 人员ID
     */
    private String personId;
    
    /**
     * 控制记录库ID（来自设备）
     */
    private Integer recordId;
    
    /**
     * 认证结果：1=允许，2=拒绝，22=待核验
     */
    private Integer verifyStatus;
    
    /**
     * 验证类型
     */
    private Integer verifyType;
    
    /**
     * 人员类型：0=白名单，1=黑名单
     */
    private Integer personType;
    
    /**
     * 人脸相似度1
     */
    private Double similarity;
    
    /**
     * 人脸相似度2
     */
    private Double similarity2;
    
    /**
     * 发送时间标识
     */
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
     * 人员姓名（来自RecInfo.Name字段）
     */
    private String name;
    
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
     * 性别
     */
    private String gender;
    
    /**
     * 民族
     */
    private String nation;
    
    /**
     * 证件类型
     */
    private String cardType;
    
    /**
     * 出生日期
     */
    private String birthday;
    
    /**
     * 籍贯
     */
    private String nativePlace;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 门禁卡来源
     */
    private String mjCardFrom;
    
    /**
     * 门禁卡号
     */
    private String mjCardNo;
    
    /**
     * 临时有效
     */
    private String tempValid;
    
    /**
     * 有效开始时间
     */
    private String validBegin;
    
    /**
     * 有效结束时间
     */
    private String validEnd;
    
    /**
     * 人员UUID
     */
    private String personUuid;
    
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
    private String pushType;
    
    /**
     * 开门方式
     */
    private String opendoorWay;
    
    /**
     * 卡号2
     */
    private String cardNum2;
    
    /**
     * RFID卡号
     */
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
     * 抓拍图片路径
     */
    private String imagePath;
    
    /**
     * 抓拍图片数据
     */
    private String sanpPic;
    
    /**
     * 识别时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recognitionTime;
    
    /**
     * 体温（若支持）
     */
    private Double temperature;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 关联的智能设备ID
     */
    private Long smartDeviceId;
    
    /**
     * 关联的老人ID
     */
    private Long elderlyId;
    
    /**
     * 关联的机构ID
     */
    private Long organizationId;
    
    /**
     * 处理状态：PENDING-待处理，PROCESSED-已处理
     */
    private String processStatus;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 数据库创建时间字段（对应created_at）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 数据库更新时间字段（对应updated_at）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除（软删除标记）
     */
    private Boolean isDeleted;
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setRecognitionTime(LocalDateTime recognitionTime) {
        this.recognitionTime = recognitionTime;
    }
    
    public LocalDateTime getRecognitionTime() {
        return recognitionTime;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setElderlyId(Long elderlyId) {
        this.elderlyId = elderlyId;
    }
    
    public Long getElderlyId() {
        return elderlyId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getVerifyStatus() {
        return verifyStatus;
    }
    
    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }
    
    public Double getSimilarity() {
        return similarity;
    }
    
    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }
    
    public String getCustomId() {
        return customId;
    }
    
    public void setCustomId(String customId) {
        this.customId = customId;
    }
    
    public Integer getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Long getSmartDeviceId() {
        return smartDeviceId;
    }
    
    public void setSmartDeviceId(Long smartDeviceId) {
        this.smartDeviceId = smartDeviceId;
    }
    
    public String getProcessStatus() {
        return processStatus;
    }
    
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getNation() {
        return nation;
    }
    
    public void setNation(String nation) {
        this.nation = nation;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public String getNativePlace() {
        return nativePlace;
    }
    
    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getMjCardFrom() {
        return mjCardFrom;
    }
    
    public void setMjCardFrom(String mjCardFrom) {
        this.mjCardFrom = mjCardFrom;
    }
    
    public String getMjCardNo() {
        return mjCardNo;
    }
    
    public void setMjCardNo(String mjCardNo) {
        this.mjCardNo = mjCardNo;
    }
    
    public String getTempValid() {
        return tempValid;
    }
    
    public void setTempValid(String tempValid) {
        this.tempValid = tempValid;
    }
    
    public String getValidBegin() {
        return validBegin;
    }
    
    public void setValidBegin(String validBegin) {
        this.validBegin = validBegin;
    }
    
    public String getValidEnd() {
        return validEnd;
    }
    
    public void setValidEnd(String validEnd) {
        this.validEnd = validEnd;
    }
    
    public String getPersonUuid() {
        return personUuid;
    }
    
    public void setPersonUuid(String personUuid) {
        this.personUuid = personUuid;
    }
    
    public Integer getVerifyType() {
        return verifyType;
    }
    
    public void setVerifyType(Integer verifyType) {
        this.verifyType = verifyType;
    }
    
    public String getSanpPic() {
        return sanpPic;
    }
    
    public void setSanpPic(String sanpPic) {
        this.sanpPic = sanpPic;
    }
}