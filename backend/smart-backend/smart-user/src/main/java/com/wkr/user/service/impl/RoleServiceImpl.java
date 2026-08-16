package com.wkr.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkr.core.exception.BusinessException;
import com.wkr.user.entity.Role;
import com.wkr.user.entity.UserRole;
import com.wkr.user.mapper.RoleMapper;
import com.wkr.user.mapper.UserRoleMapper;
import com.wkr.user.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(
            RoleMapper roleMapper,
            UserRoleMapper userRoleMapper
    ) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<Role> listRoles() {

        return roleMapper.selectList(null);
    }

    @Override
    public Role getRoleById(Long id) {

        Role role = roleMapper.selectById(id);

        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }

        return role;
    }

    @Override
    public Long createRole(String roleName) {

        if (roleName == null || roleName.isBlank()) {
            throw new BusinessException(400, "角色名称不能为空");
        }

        String name = roleName.trim();

        Role exists = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleName, name)
        );

        if (exists != null) {
            throw new BusinessException(409, "角色已存在");
        }

        Role role = new Role();
        role.setRoleName(name);

        roleMapper.insert(role);

        return role.getId();
    }

    @Override
    public void updateRole(Long id, String roleName) {

        Role role = getRoleById(id);

        if (roleName == null || roleName.isBlank()) {
            throw new BusinessException(400, "角色名称不能为空");
        }

        String name = roleName.trim();

        if (name.equals(role.getRoleName())) {
            return;
        }

        Role exists = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleName, name)
        );

        if (exists != null
                && !exists.getId().equals(id)) {
            throw new BusinessException(409, "角色已存在");
        }

        role.setRoleName(name);

        roleMapper.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {

        Role role = getRoleById(id);

        /*
         * 先删除用户角色关联。
         *
         * 数据库虽然配置了 ON DELETE CASCADE，
         * 这里显式删除更加直观，也避免业务层依赖数据库级联行为。
         */
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getRoleId, id)
        );

        roleMapper.deleteById(role.getId());
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {

        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    public void assignRole(Long userId, Long roleId) {

        /*
         * 先确认角色存在
         */
        getRoleById(roleId);

        /*
         * 检查是否已经拥有该角色
         */
        UserRole exists = userRoleMapper.selectOne(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getRoleId, roleId)
        );

        if (exists != null) {
            throw new BusinessException(409, "用户已经拥有该角色");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);

        userRoleMapper.insert(userRole);
    }

    @Override
    public void removeRole(Long userId, Long roleId) {

        int rows = userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getRoleId, roleId)
        );

        if (rows == 0) {
            throw new BusinessException(404, "用户没有该角色");
        }
    }
}