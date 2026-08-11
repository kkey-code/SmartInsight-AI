package com.wkr.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.wkr.user.mapper")
@ComponentScan({"com.wkr.user","com.wkr.web","com.wkr.apiuser"})
public class SmartUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartUserApplication.class, args);
    }

}
