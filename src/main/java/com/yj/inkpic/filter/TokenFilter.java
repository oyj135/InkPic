package com.yj.inkpic.filter;

import cn.hutool.extra.tokenizer.TokenizerUtil;
import com.yj.inkpic.constant.JwtClaimsConstant;
import com.yj.inkpic.properties.JwtProperties;
import com.yj.inkpic.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/18
 *
 * 令牌过滤器
 * 登录和注册请求不需要效验令牌
 */
@Slf4j
//@WebFilter("/*") // 拦截所有请求
public class TokenFilter implements Filter {

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1.获取到请求路径
        String requestURI = request.getRequestURI();

        // 2.判断是否是登录和注册请求,如果是,则直接放行
        if (requestURI.contains("/login") || requestURI.contains("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3.获取请求头的token
        String token = request.getHeader("token");

        // 4. 效验 token 是否存在
        if (token == null || token.isEmpty()) {
            log.info("token不存在");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // 5.如果 token 存在，效验令牌
        try {
            JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        } catch (Exception e) {
            log.info("token效验失败, 响应 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 6.如果 token 有效，则放行
        log.info("token效验成功, 放行");
        filterChain.doFilter(request, response);
    }
}
