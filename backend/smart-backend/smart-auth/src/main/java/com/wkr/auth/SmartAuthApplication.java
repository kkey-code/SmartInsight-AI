package com.wkr.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wkr.auth"})
@EnableFeignClients(basePackages = {"com.wkr.apiuser.feign"})
public class SmartAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartAuthApplication.class, args);
    }
}