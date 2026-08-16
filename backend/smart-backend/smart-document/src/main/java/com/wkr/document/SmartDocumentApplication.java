package com.wkr.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.wkr.document", "com.wkr.web"})
public class SmartDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartDocumentApplication.class, args);
    }

}
