package com.wkr.auth.service;

import com.wkr.auth.dto.LoginDTO;
import com.wkr.auth.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

}

