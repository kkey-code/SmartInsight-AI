package com.wkr.web.exception;

import com.wkr.core.exception.BusinessException;
import com.wkr.core.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {

        return Result.error(
                e.getCode(),
                e.getMessage()
        );
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {

        e.printStackTrace();

        return Result.error(
                500,
                "系统异常"
        );
    }

    /**
     * 参数校验异常 - 处理 @Valid 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        return Result.error(400, message);
    }

    /**
     * 运行时异常 - 兜底处理所有未捕获的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error(500, e.getMessage());
    }
}
