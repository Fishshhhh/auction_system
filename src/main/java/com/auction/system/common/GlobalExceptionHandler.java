package com.auction.system.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseResult<String> handleException(Exception e) {
        e.printStackTrace();
        return ResponseResult.error("系统内部错误：" + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<String> handleRuntimeException(RuntimeException e) {
        return ResponseResult.error("业务异常：" + e.getMessage());
    }
}