package com.wkr.auth.vo;

import com.wkr.apiuser.dto.RoleDTO;
import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    private String token;
    private Long userId;
    private String username;
    private List<RoleDTO> roles;

}