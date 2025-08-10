package com.example.pension.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 服务项目名称
     */
    private String title;
    
    /**
     * 服务项目描述
     */
    private String description;
    
    /**
     * 服务分类
     */
    private String category;
    
    /**
     * 服务价格
     */
    private BigDecimal price;
    
    /**
     * 服务时长（分钟）
     */
    private Integer duration;
    
    /**
     * 服务图片URL
     */
    private String imageUrl;
    
    /**
     * 服务标签（JSON格式）
     */
    private String tags;
    
    /**
     * 服务提供机构ID
     */
    private Long organizationId;
    
    /**
     * 服务提供机构名称
     */
    private String organizationName;
    
    /**
     * 服务状态（ACTIVE-启用, INACTIVE-停用）
     */
    private String status;
    
    /**
     * 服务评分
     */
    private BigDecimal rating;
    
    /**
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 服务区域
     */
    private String serviceArea;
    
    /**
     * 服务时间（JSON格式，包含可服务的时间段）
     */
    private String serviceTime;
    
    /**
     * 是否支持紧急服务
     */
    private Boolean emergencySupport;
    
    /**
     * 服务要求
     */
    private String requirements;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 更新人ID
     */
    private Long updatedBy;
    
    /**
     * 备注
     */
    private String remarks;
}