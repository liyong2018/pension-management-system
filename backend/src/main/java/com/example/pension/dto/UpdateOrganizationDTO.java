package com.example.pension.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOrganizationDTO {
    @Size(max = 255, message = "机构名称长度不能超过255个字符")
    private String name; // 更新时，名称可能是可选的，或者不允许更改，取决于业务规则

    @Size(max = 100, message = "机构类型长度不能超过100个字符")
    private String type;

    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPersonPhone;

    @Size(max = 100, message = "联系人姓名长度不能超过100个字符")
    private String contactPersonName;

    private String description;
} 