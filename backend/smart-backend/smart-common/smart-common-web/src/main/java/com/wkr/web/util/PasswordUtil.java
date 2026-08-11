package com.wkr.web.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密
     */
    public static String encode(String password){

        return encoder.encode(password);
    }

    /**
     * 密码校验
     */
    public static boolean matches(String rawPassword, String encodePassword){

        return encoder.matches(
                rawPassword,
                encodePassword
        );
    }
}