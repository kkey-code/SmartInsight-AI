package com.wkr.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("role")
public class Role {

    private Long id;

    private String roleName;

    private LocalDateTime createTime;
}
