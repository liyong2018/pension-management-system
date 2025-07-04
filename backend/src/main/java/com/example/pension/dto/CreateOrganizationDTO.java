package com.example.pension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@Data
public class CreateOrganizationDTO {
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 255, message = "机构名称长度不能超过255个字符")
    private String name;

    @Size(max = 100, message = "机构简称长度不能超过100个字符")
    private String shortName;

    @NotBlank(message = "机构类型不能为空")
    @Size(max = 50, message = "机构类型长度不能超过50个字符")
    private String type;

    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 255, message = "网址长度不能超过255个字符")
    private String website;

    private LocalDate establishmentDate;

    @Size(max = 100, message = "许可证号长度不能超过100个字符")
    private String licenseNumber;

    @Size(max = 1000, message = "经营范围长度不能超过1000个字符")
    private String businessScope;

    @Min(value = 0, message = "床位数量不能小于0")
    private Integer bedCount;

    @Min(value = 0, message = "实际服务人数不能小于0")
    private Integer actualServiceCount;

    @Size(max = 1000, message = "收费标准长度不能超过1000个字符")
    private String chargingStandard;

    @Size(max = 100, message = "面积长度不能超过100个字符")
    private String area;

    @JsonIgnore
    private Long directorUserId; // 前端选择的负责人用户ID，不存储到数据库

    @NotBlank(message = "负责人姓名不能为空")
    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String directorName;

    @NotBlank(message = "负责人联系方式不能为空")
    @Size(max = 20, message = "负责人联系方式长度不能超过20个字符")
    private String directorContact;

    @Min(value = 0, message = "员工总数不能小于0")
    private Integer employeeCount;

    @Min(value = 0, message = "专业护理人员数量不能小于0")
    private Integer professionalNurseCount;

    @Size(max = 100, message = "消防许可证号长度不能超过100个字符")
    private String fireLicense;

    @Size(max = 100, message = "卫生许可证号长度不能超过100个字符")
    private String sanitaryLicense;

    @Size(max = 100, message = "医疗机构执业许可证号长度不能超过100个字符")
    private String medicalLicense;

    @Size(max = 1000, message = "其他资质证书长度不能超过1000个字符")
    private String otherQualifications;

    @Size(max = 1000, message = "机构描述长度不能超过1000个字符")
    private String description;
} 