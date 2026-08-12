package com.wkr.apiuser.feign;

import com.wkr.apiuser.dto.PasswordVerifyDTO;
import com.wkr.apiuser.dto.UserDTO;
import com.wkr.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "smart-user")
public interface UserFeignClient {

    @GetMapping("/inner/user/{username}")
    UserDTO getByUsername(@PathVariable("username") String username);

    @GetMapping("/inner/user/id/{userId}")
    UserDTO getById(@PathVariable("userId") Long userId);

    @PostMapping("/inner/user/verify-password")
    Result<Boolean> verifyPassword(@RequestBody PasswordVerifyDTO dto);
}