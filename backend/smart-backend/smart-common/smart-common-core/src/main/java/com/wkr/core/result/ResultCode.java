package com.wkr.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200,"success"),
    ERROR(500,"系统异常"),
    PARAM_ERROR(400,"参数错误"),
    NOT_FOUND(404,"数据不存在");

    private final Integer code;
    private final String message;

}