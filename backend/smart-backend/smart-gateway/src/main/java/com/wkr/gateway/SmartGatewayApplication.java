package com.wkr.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                SmartGatewayApplication.class,
                args
        );

    }
}