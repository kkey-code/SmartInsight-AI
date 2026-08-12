package com.wkr.user.controller;

import com.wkr.apiuser.dto.PasswordVerifyDTO;
import com.wkr.apiuser.dto.UserDTO;
import com.wkr.core.result.Result;
import com.wkr.user.entity.UserInfo;
import com.wkr.user.service.UserService;
import com.wkr.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/inner/user")
@RequiredArgsConstructor
public class InnerController {

    private final UserService userService;

    @GetMapping("/{username}")
    public UserDTO getByUsername(
            @PathVariable("username") String username
    ) {
        UserInfo userInfo = userService.getByUsername(username);
        return convertToDTO(userInfo);
    }

    @GetMapping("/id/{userId}")
    public UserDTO getById(
            @PathVariable("userId") Long userId
    ) {
        UserVO userById = userService.getUserById(userId);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userById, userDTO);
        return userDTO;
    }

    @PostMapping("/verify-password")
    public Result<Boolean> verifyPassword(
            @RequestBody PasswordVerifyDTO dto
    ) {
        try {
            Boolean b = userService.verifyPassword(dto.getUsername(), dto.getRawPassword());
            log.info("result：{}", b);
            return Result.success(b);
        } catch (Exception e) {
            return Result.error(500,"验证失败：" + e.getMessage());
        }
    }

    private UserDTO convertToDTO(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(userInfo.getId());
        dto.setUsername(userInfo.getUsername());
        dto.setEmail(userInfo.getEmail());
        dto.setStatus(userInfo.getStatus());
        dto.setCreateTime(userInfo.getCreateTime());
        return dto;
    }
}