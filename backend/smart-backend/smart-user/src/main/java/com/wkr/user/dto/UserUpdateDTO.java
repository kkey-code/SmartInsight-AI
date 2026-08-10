package com.wkr.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(min = 3, max = 20, message = "用户名长度3-20")
    private String username;

    private String email;

    private Integer status;
}