package com.wkr.user.controller;

import com.wkr.apiuser.dto.PasswordVerifyDTO;
import com.wkr.apiuser.dto.UserDTO;
import com.wkr.core.result.Result;
import com.wkr.user.dto.UserCreateDTO;
import com.wkr.user.dto.UserUpdateDTO;
import com.wkr.user.entity.UserInfo;
import com.wkr.user.service.UserService;
import com.wkr.user.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable("id") Long id){

        UserVO userById = userService.getUserById(id);
        return Result.success(userById);
    }

    @GetMapping("/username/{username}")
    public Result<UserVO> username(@PathVariable("username") String username){

        UserInfo byUsername = userService.getByUsername(username);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(byUsername, userVO);

        return Result.success(userVO);
    }

    @PostMapping
    public Result<Long> create(@RequestBody UserCreateDTO dto){

        Long id = userService.createUser(dto);
        return Result.success(id);
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto){

        userService.update(dto);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id
    ){
        userService.delete(id);
        return Result.success(null);
    }

    // 密码验证接口
    @PostMapping("/verify-password")
    public Boolean verifyPassword(@RequestBody PasswordVerifyDTO verifyDTO) {
        UserInfo user = userService.getByUsername(verifyDTO.getUsername());
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(verifyDTO.getRawPassword(), user.getPassword());
    }

    private UserDTO convertToDTO(UserInfo user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        return dto;
    }
}