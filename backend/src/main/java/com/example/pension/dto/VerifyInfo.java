package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 验证推送信息数据模型
 * 对应JSON中的info对象
 */
public class VerifyInfo {
    
    @JsonProperty("PersonID")
    private Integer personId;
    
    @JsonProperty("CreateTime")
    private String createTime;
    
    @JsonProperty("Similarity1")
    private Double similarity1;
    
    @JsonProperty("Similarity2")
    private Double similarity2;
    
    @JsonProperty("VerifyStatus")
    private Integer verifyStatus;
    
    @JsonProperty("VerfyType")
    private Integer verfyType;
    
    @JsonProperty("PersonType")
    private Integer personType;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("Gender")
    private Integer gender;
    
    @JsonProperty("Nation")
    private Integer nation;
    
    @JsonProperty("CardType")
    private Integer cardType;
    
    @JsonProperty("IdCard")
    private String idCard;
    
    @JsonProperty("Birthday")
    private String birthday;
    
    @JsonProperty("Telnum")
    private String telnum;
    
    @JsonProperty("Native")
    private String nativePlace;
    
    @JsonProperty("Address")
    private String address;
    
    @JsonProperty("Notes")
    private String notes;
    
    @JsonProperty("MjCardFrom")
    private String mjCardFrom;
    
    @JsonProperty("isNoMask")
    private Integer isNoMask;
    
    @JsonProperty("DeviceID")
    private Long deviceId;
    
    @JsonProperty("PushType")
    private Integer pushType;
    
    @JsonProperty("OpendoorWay")
    private Integer opendoorWay;
    
    @JsonProperty("szQrCodeData")
    private String szQrCodeData;
    
    @JsonProperty("MjCardNo")
    private String mjCardNo;
    
    @JsonProperty("RFIDCard")
    private String rfidCard;
    
    @JsonProperty("Tempvalid")
    private Integer tempvalid;
    
    @JsonProperty("CustomizeID")
    private Integer customizeId;
    
    @JsonProperty("PersonUUID")
    private String personUuid;
    
    @JsonProperty("ValidBegin")
    private String validBegin;
    
    @JsonProperty("ValidEnd")
    private String validEnd;
    
    @JsonProperty("Sendintime")
    private Integer sendintime;
    
    @JsonProperty("Direction")
    private Integer direction;
    
    @JsonProperty("SanpPic")
    private String sanpPic;
    
    // Getters and Setters
    public Integer getPersonId() { return personId; }
    public void setPersonId(Integer personId) { this.personId = personId; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    
    public Double getSimilarity1() { return similarity1; }
    public void setSimilarity1(Double similarity1) { this.similarity1 = similarity1; }
    
    public Double getSimilarity2() { return similarity2; }
    public void setSimilarity2(Double similarity2) { this.similarity2 = similarity2; }
    
    public Integer getVerifyStatus() { return verifyStatus; }
    public void setVerifyStatus(Integer verifyStatus) { this.verifyStatus = verifyStatus; }
    
    public Integer getVerfyType() { return verfyType; }
    public void setVerfyType(Integer verfyType) { this.verfyType = verfyType; }
    
    public Integer getPersonType() { return personType; }
    public void setPersonType(Integer personType) { this.personType = personType; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    
    public Integer getNation() { return nation; }
    public void setNation(Integer nation) { this.nation = nation; }
    
    public Integer getCardType() { return cardType; }
    public void setCardType(Integer cardType) { this.cardType = cardType; }
    
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    
    public String getTelnum() { return telnum; }
    public void setTelnum(String telnum) { this.telnum = telnum; }
    
    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getMjCardFrom() { return mjCardFrom; }
    public void setMjCardFrom(String mjCardFrom) { this.mjCardFrom = mjCardFrom; }
    
    public Integer getIsNoMask() { return isNoMask; }
    public void setIsNoMask(Integer isNoMask) { this.isNoMask = isNoMask; }
    
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    
    public Integer getPushType() { return pushType; }
    public void setPushType(Integer pushType) { this.pushType = pushType; }
    
    public Integer getOpendoorWay() { return opendoorWay; }
    public void setOpendoorWay(Integer opendoorWay) { this.opendoorWay = opendoorWay; }
    
    public String getSzQrCodeData() { return szQrCodeData; }
    public void setSzQrCodeData(String szQrCodeData) { this.szQrCodeData = szQrCodeData; }
    
    public String getMjCardNo() { return mjCardNo; }
    public void setMjCardNo(String mjCardNo) { this.mjCardNo = mjCardNo; }
    
    public String getRfidCard() { return rfidCard; }
    public void setRfidCard(String rfidCard) { this.rfidCard = rfidCard; }
    
    public Integer getTempvalid() { return tempvalid; }
    public void setTempvalid(Integer tempvalid) { this.tempvalid = tempvalid; }
    
    public Integer getCustomizeId() { return customizeId; }
    public void setCustomizeId(Integer customizeId) { this.customizeId = customizeId; }
    
    public String getPersonUuid() { return personUuid; }
    public void setPersonUuid(String personUuid) { this.personUuid = personUuid; }
    
    public String getValidBegin() { return validBegin; }
    public void setValidBegin(String validBegin) { this.validBegin = validBegin; }
    
    public String getValidEnd() { return validEnd; }
    public void setValidEnd(String validEnd) { this.validEnd = validEnd; }
    
    public Integer getSendintime() { return sendintime; }
    public void setSendintime(Integer sendintime) { this.sendintime = sendintime; }
    
    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }
    
    public String getSanpPic() { return sanpPic; }
    public void setSanpPic(String sanpPic) { this.sanpPic = sanpPic; }
}