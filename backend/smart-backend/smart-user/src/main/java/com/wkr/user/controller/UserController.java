package com.wkr.user.controller;

import com.wkr.core.result.Result;
import com.wkr.user.annotation.RequireRole;
import com.wkr.user.dto.UserCreateDTO;
import com.wkr.user.dto.UserPageDTO;
import com.wkr.user.dto.UserUpdateDTO;
import com.wkr.user.entity.UserInfo;
import com.wkr.user.service.UserService;
import com.wkr.user.vo.UserPageVO;
import com.wkr.user.vo.UserVO;
import com.wkr.web.context.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @RequireRole("ADMIN")
    @GetMapping("/page")
    public Result<UserPageVO> page(@ModelAttribute UserPageDTO dto) {
        return Result.success(userService.page(dto));
    }

    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable("id") Long id){


        UserVO userById = userService.getUserById(id);
        System.out.println(userById);
        return Result.success(userById);
    }

    @GetMapping("/username/{username}")
    public Result<UserVO> username(@PathVariable("username") String username){

        UserInfo byUsername = userService.getByUsername(username);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(byUsername, userVO);

        return Result.success(userVO);
    }

    @RequireRole("ADMIN")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateDTO dto) {

        Long id = userService.createUser(dto);
        return Result.success(id);
    }

    @RequireRole("ADMIN")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto) {

        userService.update(dto);
        return Result.success(null);
    }

    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {

        userService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/current")
    public Result<String> current() {

        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();
        List<String> roles = UserContext.getRoles();

        return Result.success(
                "userId=" + userId
                        + ", username=" + username
                        + ", roles=" + String.join(",", roles)
        );
    }
}