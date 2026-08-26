package com.yj.inkpic.config;

import com.yj.inkpic.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/18
 *
 * 拦截器配置类
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;

    /**
     * 注册 拦截器
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(tokenInterceptor)
                // 拦截路径
                .addPathPatterns("/**")
                // 排除路径
                .excludePathPatterns(
                        "/user/login",
                        "/user/code",
                        "/user/register",
                        "/user/logout",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-ui.html");
    }
}
