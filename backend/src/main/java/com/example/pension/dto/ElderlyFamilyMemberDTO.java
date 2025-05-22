package com.example.pension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ElderlyFamilyMemberDTO {
    
    private Long id;
    
    private Long elderlyId;
    
    @NotBlank(message = "家属姓名不能为空")
    private String name;
    
    private String relationship;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 