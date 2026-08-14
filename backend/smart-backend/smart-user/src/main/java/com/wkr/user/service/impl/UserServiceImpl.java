package com.wkr.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.core.exception.BusinessException;
import com.wkr.user.context.UserContext;
import com.wkr.user.dto.UserCreateDTO;
import com.wkr.user.dto.UserPageDTO;
import com.wkr.user.dto.UserUpdateDTO;
import com.wkr.user.entity.UserInfo;
import com.wkr.user.mapper.UserMapper;
import com.wkr.user.service.UserService;
import com.wkr.user.vo.UserPageVO;
import com.wkr.user.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo>implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserVO getUserById(Long id) {

        UserInfo byId = getById(id);
        if(byId == null){
            throw new BusinessException(404,"用户不存在");
        }

        return convertVO(byId);
    }

    @Override
    public UserInfo getByUsername(String username) {

        return lambdaQuery()
                .eq(UserInfo::getUsername, username)
                .one();
    }

    @Override
    public Long createUser(UserCreateDTO dto){

        UserInfo exists = getByUsername(dto.getUsername());

        if (exists != null) {
            throw new BusinessException(409, "用户名已存在");
        }

        UserInfo user = new UserInfo();

        user.setUsername(dto.getUsername());

        // BCrypt加密
        user.setPassword(
                passwordEncoder.encode(dto.getPassword()));

        user.setEmail(dto.getEmail());
        user.setStatus(0);
        user.setDeleted(0);
        save(user);

        return user.getId();
    }

    @Override
    public UserPageVO page(UserPageDTO dto) {

        Page<UserInfo> page =
                new Page<>(
                        dto.getPage(),
                        dto.getSize()
                );
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(
                dto.getUsername() != null,
                UserInfo::getUsername,
                dto.getUsername()
        );

        Page<UserInfo> result =page(page, wrapper);

        List<UserVO> list =
                result.getRecords()
                        .stream()
                        .map(this::convertVO)
                        .collect(Collectors.toList());

        UserPageVO vo = new UserPageVO();
        vo.setTotal(result.getTotal());
        vo.setPage(dto.getPage());
        vo.setSize(dto.getSize());
        vo.setRecords(list);

        return vo;
    }

    @Override
    public void update(UserUpdateDTO dto) {

        UserInfo user = getById(dto.getId());

        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 只有修改用户名时才检查重复
        if (dto.getUsername() != null
                && !dto.getUsername().equals(user.getUsername())) {

            UserInfo byUsername = getByUsername(dto.getUsername());

            if (byUsername != null
                    && !byUsername.getId().equals(user.getId())) {
                throw new BusinessException(409, "用户名已存在");
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        updateById(user);
    }

    @Override
    public void delete(Long id) {

        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权删除用户");
        }
        UserInfo user = getById(id);

        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 逻辑删除
        user.setDeleted(1);
        updateById(user);
    }

    @Override
    public Boolean verifyPassword(String username, String rawPassword) {

        UserInfo user = getByUsername(username);

        if (user == null) {return false;}

        // 禁用用户禁止登录
        if (Integer.valueOf(1).equals(user.getStatus())) {
            return false;
        }

        return passwordEncoder.matches(
                rawPassword,
                user.getPassword()
        );
    }

    private UserVO convertVO(UserInfo user){

        UserVO vo = new UserVO();

        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        return vo;
    }

}