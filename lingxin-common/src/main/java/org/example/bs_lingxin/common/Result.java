package org.example.bs_lingxin.common;

import lombok.Data;
import org.example.bs_lingxin.common.enums.ResultCode;

/**
 * 全局统一返回结果封装
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    /**
     * 通用失败，状态码走兜底的 999 未知错误。
     */
    public static <T> Result<T> fail() {
        return build(ResultCode.UNKNOWN, null);
    }

    /**
     * 指定状态码，提示信息取枚举默认文案。
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return build(resultCode, null);
    }

    /**
     * 指定状态码，但覆盖提示信息
     */
    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        Result<T> r = new Result<>();
        r.setCode(resultCode.getCode());
        r.setMsg(msg);
        return r;
    }

    /**
     * 保留原始 code + msg 的写法，兼容历史调用与外部状态码。
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    private static <T> Result<T> build(ResultCode resultCode, T data) {
        Result<T> r = new Result<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMessage());
        r.setData(data);
        return r;
    }
}
