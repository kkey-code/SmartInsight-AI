package com.wkr.user.service;

import com.wkr.user.entity.Role;
import java.util.List;

public interface RoleService {

    /**
     * 根据用户ID查询角色
     */
    List<Role> getRolesByUserId(Long userId);
}