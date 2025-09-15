package com.example.pension.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * OneNET推送接口演示应用
 * 这是一个独立的Spring Boot应用程序，用于快速部署测试OneNET平台的数据推送
 */
@SpringBootApplication
@ComponentScan({"com.example.pension.demo"})
public class OneNetPushDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneNetPushDemoApplication.class, args);
    }
}