package com.wkr.user.service;

import com.wkr.user.dto.UserCreateDTO;
import com.wkr.user.dto.UserPageDTO;
import com.wkr.user.dto.UserUpdateDTO;
import com.wkr.user.entity.UserInfo;
import com.wkr.user.vo.UserPageVO;
import com.wkr.user.vo.UserVO;

public interface UserService {

    /**
     * 根据ID查询用户
     */
    UserVO getUserById(Long id);

    /**
     * 根据用户名查询
     */
    UserInfo getByUsername(String username);

    /**
     * 创建用户
     */
    Long createUser(UserCreateDTO dto);

    /**
     * 分页查询
     */
    UserPageVO page(UserPageDTO dto);

    void update(UserUpdateDTO dto);

    void delete(Long id);

}