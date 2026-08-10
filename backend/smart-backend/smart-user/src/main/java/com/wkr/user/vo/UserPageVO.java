package com.wkr.user.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserPageVO {

    /**
     * 总数量
     */
    private Long total;

    /**
     * 当前页
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;

    /**
     * 数据列表
     */
    private List<UserVO> records;

}