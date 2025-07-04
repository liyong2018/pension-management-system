package com.example.pension.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;

    private String username;

    private String passwordHash;

    private String fullName;

    private String email;

    private String phone;



    private Organization organization;

    private boolean isAdmin;

    private boolean isActive = true;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}