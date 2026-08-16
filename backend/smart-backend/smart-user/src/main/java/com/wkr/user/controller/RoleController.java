package com.wkr.user.controller;

import com.wkr.core.result.Result;
import com.wkr.user.annotation.RequireRole;
import com.wkr.user.entity.Role;
import com.wkr.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 查询角色列表
     */
    @RequireRole("ADMIN")
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(
                roleService.listRoles()
        );
    }

    /**
     * 查询角色
     */
    @RequireRole("ADMIN")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable("id") Long id) {
        return Result.success(
                roleService.getRoleById(id)
        );
    }

    /**
     * 创建角色
     */
    @RequireRole("ADMIN")
    @PostMapping
    public Result<Long> create(@RequestParam("roleName") String roleName) {  // ✅ 已指定
        Long id = roleService.createRole(roleName);
        return Result.success(id);
    }

    /**
     * 修改角色
     */
    @RequireRole("ADMIN")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable("id") Long id,
            @RequestParam("roleName") String roleName
    ) {
        roleService.updateRole(id, roleName);
        return Result.success(null);
    }

    /**
     * 删除角色
     */
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return Result.success(null);
    }

    /**
     * 查询用户角色
     */
    @RequireRole("ADMIN")
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getUserRoles(@PathVariable("userId") Long userId) {  // ✅ 已指定
        return Result.success(
                roleService.getRolesByUserId(userId)
        );
    }

    /**
     * 给用户分配角色
     */
    @RequireRole("ADMIN")
    @PostMapping("/user/{userId}/{roleId}")
    public Result<Void> assignRole(
            @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId
    ) {
        roleService.assignRole(userId, roleId);
        return Result.success(null);
    }

    /**
     * 移除用户角色
     */
    @RequireRole("ADMIN")
    @DeleteMapping("/user/{userId}/{roleId}")
    public Result<Void> removeRole(
            @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId
    ) {
        roleService.removeRole(userId, roleId);
        return Result.success(null);
    }
}