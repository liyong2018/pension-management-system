package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemUser {

    private Long id;

    private String username;

    private String passwordHash;

    private String fullName;

    private String email;

    private String phone;

    private Long organizationId;

    private String organizationName;

    private Boolean isAdmin = false;

    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联角色列表
    private List<Role> roles;
}