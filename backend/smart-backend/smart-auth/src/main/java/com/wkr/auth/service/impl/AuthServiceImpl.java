package com.wkr.auth.service.impl;

import com.wkr.apiuser.dto.PasswordVerifyDTO;
import com.wkr.apiuser.dto.UserDTO;
import com.wkr.apiuser.feign.UserFeignClient;
import com.wkr.auth.dto.LoginDTO;
import com.wkr.auth.service.AuthService;
import com.wkr.auth.vo.LoginVO;
import com.wkr.core.exception.BusinessException;
import com.wkr.core.result.Result;
import com.wkr.core.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public LoginVO login(LoginDTO dto) {

        //  返回的是 UserDTO，不是 Entity
        UserDTO user = userFeignClient.getByUsername(dto.getUsername());

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2.  通过专门的接口验证密码
        PasswordVerifyDTO verifyDTO = new PasswordVerifyDTO();
        verifyDTO.setUsername(dto.getUsername());
        verifyDTO.setRawPassword(dto.getPassword());

        Result<Boolean> result = userFeignClient.verifyPassword(verifyDTO);

        if (result == null || !result.getData()) {
            log.warn("密码错误，用户名：{}", dto.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        //  生成 Token
        String token = JwtUtil.createToken(user.getId(), user.getUsername());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());

        return vo;
    }
}