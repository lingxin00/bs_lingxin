package org.example.bs_lingxin.common.constant;

/**
 * 登录端标识（Sa-Token 的 device）
 *
 * 三端共用同一个 StpUtil，靠 device 区分身份：
 * 登录时写入 device，拦截器按请求路径前缀校验 device 是否匹配，
 * 避免商家 token 拿去调用户端或管理端接口。
 *
 * 注意：device 只解决「谁能访问哪个前缀」，token 与登录 id 的绑定本来就是一对一，
 * 所以 StpUtil.getLoginIdAsLong() 拿到的一定是当前 token 所属账号的 id。
 */
public final class LoginDevice {

    /** 平台管理员：/api/admin/** */
    public static final String ADMIN = "admin";

    /** 商家：/api/merchant/** */
    public static final String MERCHANT = "merchant";

    /** C 端用户：/api/user/**、/api/user-address/** */
    public static final String USER = "user";

    private LoginDevice() {
    }
}
