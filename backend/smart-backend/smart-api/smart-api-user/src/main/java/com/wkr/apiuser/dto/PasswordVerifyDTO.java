package com.wkr.apiuser.dto;

import lombok.Data;

@Data
public class PasswordVerifyDTO {
    private String username;
    private String rawPassword;  // 明文密码
}