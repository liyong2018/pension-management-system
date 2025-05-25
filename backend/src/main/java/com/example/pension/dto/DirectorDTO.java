package com.example.pension.dto;

import lombok.Data;

@Data
public class DirectorDTO {
    private Long userId;
    private String username;
    private String directorName;
    private String directorContact;
    private String email;
    private Long organizationId;
    private String organizationName;
} 