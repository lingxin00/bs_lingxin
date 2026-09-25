package org.example.bs_lingxin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 采用sa-token，
 * 登录拦截器
 * */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())    //传入拦截器实例对象 SaInterceptor 是 Sa‑Token 提供的内置拦截器
                .addPathPatterns("/**")         //拦截对象为/下的所有接口
                .excludePathPatterns("/admin/login","/user/login","/user/login");   //放行对应接口

    }
}
