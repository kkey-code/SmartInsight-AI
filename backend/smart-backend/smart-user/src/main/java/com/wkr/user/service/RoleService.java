package com.wkr.user.service;

import com.wkr.user.entity.Role;

import java.util.List;

public interface RoleService {

    /**
     * 查询全部角色
     */
    List<Role> listRoles();

    /**
     * 根据ID查询角色
     */
    Role getRoleById(Long id);

    /**
     * 创建角色
     */
    Long createRole(String roleName);

    /**
     * 修改角色
     */
    void updateRole(Long id, String roleName);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 查询用户拥有的角色
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 给用户分配角色
     */
    void assignRole(Long userId, Long roleId);

    /**
     * 移除用户角色
     */
    void removeRole(Long userId, Long roleId);
}