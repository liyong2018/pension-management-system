package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {

    private Long id;

    private String roleName;

    private String roleKey;

    private String description;

    private String status = "1"; // 1:启用 0:禁用

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联权限列表
    private List<MenuPermission> permissions;
} 