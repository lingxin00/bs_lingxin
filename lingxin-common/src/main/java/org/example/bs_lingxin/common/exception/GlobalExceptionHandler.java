package org.example.bs_lingxin.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 获取抛出的BusinessException异常
 * 获取抛出的除了BusinessException的异常
 * */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
//   捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessException(BusinessException businessException){
        log.error("业务异常：{}",businessException.getMessage());
        return Result.fail(businessException.getCode(),businessException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> allException(Exception e){
        log.error("系统未知异常",e);
        return Result.fail(ResultCode.INTERNAL_SERVER_ERROR,"服务器内部错误，请稍后重试");
    }


}
