package org.example.bs_lingxin.framework.config.SaTokenConfig;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.example.bs_lingxin.common.constant.LoginDevice;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 采用sa-token，
 * 登录拦截器 + 多端身份隔离
 * */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 1. 校验登录：没带 token / token 失效直接抛 NotLoginException
                    StpUtil.checkLogin();
                    // 2. 校验端：token 里的 device 必须和请求路径前缀对应的端一致
                    checkDevice(SaHolder.getRequest().getRequestPath());
                }))
                .addPathPatterns("/**")         //拦截对象为/下的所有接口
                // 放行免登录接口：管理员登录、用户登录、用户注册、商家登录
                .excludePathPatterns("/api/admin/login", "/api/user/login", "/api/user/register", "/api/merchant/login");
    }

    /**
     * 按路径前缀校验登录端
     *
     * admin / merchant / user 三端共用同一个 StpUtil，不校验 device 的话：
     * 商家 token 能调 /api/user/info，用户 token 能调 /api/user/page 拉全站用户。
     *
     * 注意 /api/merchant/** 和 /api/user/** 里混了两类接口：
     * - info / profile / logout 是本人自助，只认本端身份
     * - page / {id} / status / add 是后台管理，只认管理端身份
     * /api/user-address/** 全部是用户自助，/api/admin/** 全部是管理端。
     */
    private void checkDevice(String path) {
        if (path == null) {
            return;
        }
        String device = StpUtil.getLoginDevice();

        if (path.startsWith("/api/admin/") && !LoginDevice.ADMIN.equals(device)) {
            throw new BusinessException("当前登录身份不能访问管理端接口", 403);
        }

        if (path.startsWith("/api/merchant/")) {
            boolean selfService = isOneOf(path, "/api/merchant/info", "/api/merchant/profile", "/api/merchant/logout");
            requireDevice(device, selfService ? LoginDevice.MERCHANT : LoginDevice.ADMIN);
        }

        if (path.startsWith("/api/user/")) {
            boolean selfService = isOneOf(path, "/api/user/info", "/api/user/profile");
            requireDevice(device, selfService ? LoginDevice.USER : LoginDevice.ADMIN);
        }

        if (path.startsWith("/api/user-address/")) {
            requireDevice(device, LoginDevice.USER);
        }
    }

    private boolean isOneOf(String path, String... candidates) {
        for (String candidate : candidates) {
            if (candidate.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private void requireDevice(String device, String expected) {
        if (!expected.equals(device)) {
            throw new BusinessException("当前登录身份不能访问该接口", 403);
        }
    }
}
