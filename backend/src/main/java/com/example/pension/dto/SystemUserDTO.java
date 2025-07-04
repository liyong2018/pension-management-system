package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemUserDTO {

    private Long id;

    private String username;

    private String password; // 用于接收前端传入的明文密码

    private String fullName;

    private String email;

    private String phone;

    private Long organizationId;

    private String organizationName;

    private Long serviceStaffId;

    private Boolean isAdmin = false;

    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 角色ID列表（用于前端传递）
    private List<Long> roleIds;

    // 角色信息列表（用于前端展示）
    private List<RoleDTO> roles;
}