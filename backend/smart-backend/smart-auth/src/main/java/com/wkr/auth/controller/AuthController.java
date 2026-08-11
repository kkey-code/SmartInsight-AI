package com.wkr.auth.controller;

import com.wkr.auth.dto.LoginDTO;
import com.wkr.auth.service.AuthService;
import com.wkr.auth.vo.LoginVO;
import com.wkr.core.result.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto){

        return Result.success(
                authService.login(dto)
        );

    }

}