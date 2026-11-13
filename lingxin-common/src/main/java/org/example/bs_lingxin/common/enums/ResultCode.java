package org.example.bs_lingxin.common.enums;

import lombok.Getter;

/**
 * 全局统一状态码。
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "成功"),

    /** 请求参数不正确，如 @Valid 校验失败 */
    BAD_REQUEST(400, "请求参数不正确"),
    /** 账号未登录，未携带 token 或 token 失效 */
    UNAUTHORIZED(401, "账号未登录"),
    /** 没有该操作权限，权限不足 */
    FORBIDDEN(403, "没有该操作权限"),
    /** 请求未找到，接口路径错误 */
    NOT_FOUND(404, "请求未找到"),
    /** 请求方法不正确，GET/POST 用错 */
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    /** 请求失败，请稍后重试，并发请求冲突（锁竞争） */
    LOCKED(423, "请求失败，请稍后重试"),
    /** 请求过于频繁，触发限流 */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    /** 系统异常，后端未捕获异常 */
    INTERNAL_SERVER_ERROR(500, "系统异常"),
    /** 功能未实现/未开启，功能开关未启用 */
    NOT_IMPLEMENTED(501, "功能未实现/未开启"),
    /** 错误的配置项，配置缺失或不合法 */
    BAD_CONFIGURATION(502, "错误的配置项"),

    /** 重复请求，幂等校验拦截 */
    REPEAT_REQUEST(900, "重复请求"),
    /** 演示模式，禁止写操作 */
    DEMO_MODE(901, "演示模式，禁止写操作"),

    /** 未知错误，兜底 */
    UNKNOWN(999, "未知错误");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码反查枚举，找不到返回 null。
     */
    public static ResultCode of(Integer code) {
        if (code == null) {
            return null;
        }
        int value = code;
        for (ResultCode resultCode : values()) {
            if (resultCode.code == value) {
                return resultCode;
            }
        }
        return null;
    }

    /**
     * 判断是否为成功状态码。
     */
    public static boolean isSuccess(Integer code) {
        return code != null && code == SUCCESS.code;
    }
}
