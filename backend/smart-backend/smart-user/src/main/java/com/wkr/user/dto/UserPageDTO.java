package com.wkr.user.dto;

import lombok.Data;

@Data
public class UserPageDTO {

    /**
     * 当前页
     */
    private Long page = 1L;

    /**
     * 每页数量
     */
    private Long size = 10L;

    /**
     * 用户名搜索
     */
    private String username;


}