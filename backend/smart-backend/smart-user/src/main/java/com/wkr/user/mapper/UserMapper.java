package com.wkr.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkr.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper
        extends BaseMapper<UserInfo> {


}