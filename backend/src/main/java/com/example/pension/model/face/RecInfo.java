package com.example.pension.model.face;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 认证记录信息实体
 * 用于接收人脸识别认证推送数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecInfo {
    
    /**
     * 人员唯一ID
     */
    @JsonProperty("CustomID")
    private String customId;
    
    /**
     * 自定义ID
     */
    @JsonProperty("CustomizeID")
    private Integer customizeId;
    
    /**
     * 人员ID
     */
    @JsonProperty("PersonID")
    private String personId;
    
    /**
     * 人员UUID
     */
    @JsonProperty("PersonUUID")
    private String personUUID;
    
    /**
     * 创建时间
     */
    @JsonProperty("CreateTime")
    private String createTime;
    
    /**
     * 人员姓名
     */
    @JsonProperty("Name")
    private String name;
    
    /**
     * 性别：0=男，1=女
     */
    @JsonProperty("Gender")
    private Integer gender;
    
    /**
     * 民族
     */
    @JsonProperty("Nation")
    private Integer nation;
    
    /**
     * 证件类型
     */
    @JsonProperty("CardType")
    private Integer cardType;
    
    /**
     * 身份证号
     */
    @JsonProperty("IdCard")
    private String idCard;
    
    /**
     * 生日
     */
    @JsonProperty("Birthday")
    private String birthday;
    
    /**
     * 电话号码
     */
    @JsonProperty("Telnum")
    private String telnum;
    
    /**
     * 籍贯
     */
    @JsonProperty("Native")
    private String nativePlace;
    
    /**
     * 地址
     */
    @JsonProperty("Address")
    private String address;
    
    /**
     * 备注
     */
    @JsonProperty("Notes")
    private String notes;
    
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
     * 认证类型
     */
    @JsonProperty("VerfyType")
    private Integer verfyType;
    
    /**
     * 人员类型：0=白名单，1=黑名单
     */
    @JsonProperty("PersonType")
    private Integer personType;
    
    /**
     * 人脸相似度1
     */
    @JsonProperty("Similarity1")
    private Double similarity1;
    
    /**
     * 人脸相似度2
     */
    @JsonProperty("Similarity2")
    private Double similarity2;
    
    /**
     * 卡来源
     */
    @JsonProperty("MjCardFrom")
    private String mjCardFrom;
    
    /**
     * 卡号
     */
    @JsonProperty("MjCardNo")
    private String mjCardNo;
    
    /**
     * 临时有效
     */
    @JsonProperty("Tempvalid")
    private Integer tempvalid;
    
    /**
     * 有效开始时间
     */
    @JsonProperty("ValidBegin")
    private String validBegin;
    
    /**
     * 有效结束时间
     */
    @JsonProperty("ValidEnd")
    private String validEnd;
    
    /**
     * 发送时间标识
     */
    @JsonProperty("Sendintime")
    private Integer sendintime;
    
    /**
     * 通行方向：1=进入，2=退出
     */
    @JsonProperty("Direction")
    private Integer direction;
    
    /**
     * 操作类型
     */
    @JsonProperty("Otype")
    private Integer otype;
    
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
    @JsonProperty("DeviceID")
    private String facesluiceId;
    
    /**
     * 闸机设备名称
     */
    private String facesluiceName;
    

    
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
    @JsonProperty("RecognitionTime")
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
    @JsonProperty("szQrCodeData")
    private String szQrCodeData;
    
    /**
     * 是否戴口罩：0=戴口罩，1=未戴口罩
     */
    @JsonProperty("isNoMask")
    private Integer isNoMask;
    
    /**
     * 文件索引
     */
    @JsonProperty("dwFileIndex")
    private String dwFileIndex;
    
    /**
     * 文件位置
     */
    @JsonProperty("dwFilePos")
    private String dwFilePos;
    
    /**
     * 抓拍图片Base64编码（人脸识别图片）
     */
    @JsonProperty("SanpPic")
    private String sanpPic;
    
    /**
     * 抓拍图片Base64编码（通用字段）
     */
    private String pic;
    
    /**
     * 体温（若支持）
     */
    private Double temperature;
    
    /**
     * 设备ID（兼容字段）
     */
    // 保留占位，便于将来兼容
}