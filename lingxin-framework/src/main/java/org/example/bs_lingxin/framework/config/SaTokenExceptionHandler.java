package org.example.bs_lingxin.framework.config;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 登录异常处理
 *
 * 不加这个 advice 的话，未登录 / token 过期抛出的 NotLoginException 会被
 * lingxin-common 里 GlobalExceptionHandler 的 Exception 兜底捕获，返回 500「服务器内部错误」，
 * 前端没法据此跳登录页。
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result<?> notLogin(NotLoginException e) {
        log.warn("登录校验失败：{}", e.getMessage());
        return Result.fail(ResultCode.UNAUTHORIZED, "账号未登录或登录已过期");
    }
}
