package com.example.pension;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PensionManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PensionManagementBackendApplication.class, args);
    }

}