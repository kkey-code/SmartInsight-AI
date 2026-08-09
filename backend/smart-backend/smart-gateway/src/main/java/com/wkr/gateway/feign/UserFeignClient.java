package com.wkr.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("smart-user")
public interface UserFeignClient {

    @GetMapping("/user/info")
    String getUserInfo();

}