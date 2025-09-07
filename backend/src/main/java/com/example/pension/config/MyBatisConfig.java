package com.example.pension.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.example.pension.dao", "com.example.pension.mapper"})
public class MyBatisConfig {
    // MyBatis configuration
}