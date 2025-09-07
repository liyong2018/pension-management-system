package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 陌生人抓拍记录实体类
 * 用于存储陌生人抓拍记录
 */
@Data
public class FaceStrangerRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 抓拍库ID（来自设备）
     */
    private String snapId;
    
    /**
     * 抓拍时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime snapTime;
    
    /**
     * 抓拍图片路径
     */
    private String imagePath;
    
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
     * 关联的机构ID
     */
    private Long organizationId;
    
    /**
     * 处理状态：PENDING-待处理，PROCESSED-已处理，IGNORED-已忽略
     */
    private String processStatus;
    
    /**
     * 处理结果：ALLOWED-允许进入，DENIED-拒绝进入，REPORTED-已上报
     */
    private String processResult;
    
    /**
     * 处理人ID
     */
    private Long processedBy;
    
    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 是否删除（软删除标记）
     */
    private Boolean isDeleted;
    
    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }
    
    public String getSnapId() {
        return snapId;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setSnapTime(LocalDateTime snapTime) {
        this.snapTime = snapTime;
    }
    
    public LocalDateTime getSnapTime() {
        return snapTime;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
    
    public String getProcessStatus() {
        return processStatus;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createTime = createdAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createTime;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updateTime = updatedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updateTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }
    
    public String getProcessResult() {
        return processResult;
    }
    
    public void setProcessedBy(Long processedBy) {
        this.processedBy = processedBy;
    }
    
    public Long getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedTime = processedAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedTime;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public Long getSmartDeviceId() {
        return smartDeviceId;
    }
    
    public void setSmartDeviceId(Long smartDeviceId) {
        this.smartDeviceId = smartDeviceId;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public LocalDateTime getProcessedTime() {
        return processedTime;
    }
    
    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
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
}