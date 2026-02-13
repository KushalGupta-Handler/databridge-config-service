package com.databridge.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DatabridgeConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatabridgeConfigServiceApplication.class, args);
    }
}

