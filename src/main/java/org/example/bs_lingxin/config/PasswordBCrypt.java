package org.example.bs_lingxin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * BCrypt密码加密
 * */
@Configuration
@EnableWebSecurity
public class PasswordBCrypt {

    /**
     * BCrypt密码加密Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 关闭Security自带鉴权拦截，鉴权交给Sa‑Token
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //关闭csrf
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); //全部接口放行
        return http.build();
    }

}
