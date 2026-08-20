package com.yj.inkpic.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.yj.inkpic.constant.JwtClaimsConstant;
import com.yj.inkpic.model.dto.user.UserJwtDTO;
import com.yj.inkpic.properties.JwtProperties;
import com.yj.inkpic.utils.BaseContext;
import com.yj.inkpic.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/18
 *
 * token 拦截器
 */

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.获取请求头的token
        String token = request.getHeader("token");

        // 2. 效验 token 是否存在
        if (token == null || token.isEmpty()) {
            log.info("token不存在");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 3.如果 token 存在，效验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);

            // 获取当前登录用户信息
            Object object = claims.get(JwtClaimsConstant.USER);
            UserJwtDTO userJwtDTO = BeanUtil.toBean(object, UserJwtDTO.class);
            // 将当前登录用户信息存入 ThreadLocal
            BaseContext.setCurrentUser(userJwtDTO);
            log.info("当前登录用户：{}", userJwtDTO);
        } catch (Exception e) {
            log.info("token效验失败, 响应 401 {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 4.如果 token 有效，则放行
        log.info("token效验成功, 放行");
        return true;

    }

    /**
     *  在请求处理之后进行调用（Controller方法调用之后）
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        BaseContext.removeCurrentUser();
    }
}
