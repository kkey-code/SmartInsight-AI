package com.wkr.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.wkr.document.mapper")
@ComponentScan({"com.wkr.document", "com.wkr.web"})
public class SmartDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartDocumentApplication.class, args);
    }
}
