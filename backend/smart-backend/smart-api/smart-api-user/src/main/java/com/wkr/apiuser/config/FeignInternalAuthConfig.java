package com.wkr.apiuser.config;

import com.wkr.core.util.InternalTokenUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInternalAuthConfig {

    @Bean
    public RequestInterceptor feignInternalTokenInterceptor() {
        return template -> {
            template.header(
                    "X-Internal-Token",
                    InternalTokenUtil.getToken()
            );
        };
    }
}