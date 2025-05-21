package com.example.pension.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "机构名称不能为空")
    @Size(max = 255, message = "机构名称长度不能超过255个字符")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 100, message = "机构类型长度不能超过100个字符")
    @Column(name = "type", length = 100)
    private String type; // 例如: 公立, 私立, 社区养老中心等

    @Size(max = 255, message = "地址长度不能超过255个字符")
    @Column(name = "address")
    private String address;

    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Column(name = "contact_person_phone", length = 20)
    private String contactPersonPhone;

    @Size(max = 100, message = "联系人姓名长度不能超过100个字符")
    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 机构简介

    // 可以添加更多字段，如：
    // private String businessLicenseUrl; // 营业执照URL
    // private Integer bedCount; // 床位数
    // private String facilities; // 配套设施描述 (TEXT)
    // private String serviceScope; // 服务范围 (TEXT)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 