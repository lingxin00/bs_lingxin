package org.example.bs_lingxin.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    /**
     * 捕获 @Valid / @Validated 校验失败
     * 不加这个处理器的话，参数校验失败会落进下面的 Exception 兜底，返回 500 "服务器内部错误"，
     * 前端拿不到具体是哪个字段没填。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidException(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ResultCode.BAD_REQUEST.getMessage());
        log.warn("参数校验失败：{}", msg);
        return Result.fail(ResultCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> allException(Exception e){
        log.error("系统未知异常",e);
        return Result.fail(ResultCode.INTERNAL_SERVER_ERROR,"服务器内部错误，请稍后重试");
    }


}
