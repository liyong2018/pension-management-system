package com.example.pension.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.example.pension.dao", "com.example.pension.mapper"}, annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class MyBatisConfig {
    // MyBatis configuration
}