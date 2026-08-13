package com.wkr.apiuser.feign;

import com.wkr.apiuser.config.FeignInternalAuthConfig;
import com.wkr.apiuser.dto.PasswordVerifyDTO;
import com.wkr.apiuser.dto.RoleDTO;
import com.wkr.apiuser.dto.UserDTO;
import com.wkr.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "smart-user"
        ,configuration = FeignInternalAuthConfig.class)
public interface UserFeignClient {

    @GetMapping("/inner/user/{username}")
    UserDTO getByUsername(@PathVariable("username") String username);

    @GetMapping("/inner/user/id/{userId}")
    UserDTO getById(@PathVariable("userId") Long userId);

    @PostMapping("/inner/user/verify-password")
    Result<Boolean> verifyPassword(@RequestBody PasswordVerifyDTO dto);

    @GetMapping("/inner/user/roles/{userId}")
    List<RoleDTO> getUserRoles(@PathVariable("userId") Long userId);
}