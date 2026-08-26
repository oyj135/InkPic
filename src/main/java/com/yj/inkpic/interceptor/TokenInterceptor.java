package com.yj.inkpic.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.yj.inkpic.constant.RedisConstant;
import com.yj.inkpic.model.dto.user.UserJwtDTO;
import com.yj.inkpic.properties.JwtProperties;
import com.yj.inkpic.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        // 1.获取请求头的token
        String token = request.getHeader("token");

        // 2. 效验 token 是否存在
        if (token == null || token.isEmpty()) {
            log.info("token不存在");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 3. 从 Redis 中查询 token
        String key = RedisConstant.LOGIN_TOKEN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        if (userMap.isEmpty()) {
            log.info("用户不存在");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 4. token 有效，滑动续期（与登录 TTL 一致）
        stringRedisTemplate.expire(key, jwtProperties.getUserTtl(), TimeUnit.MILLISECONDS);

        // 5. 获取 Redis 中缓存的用户信息，存入 ThreadLocal
        UserJwtDTO userJwtDTO = BeanUtil.fillBeanWithMap(userMap, new UserJwtDTO(), false);
        BaseContext.setCurrentUser(userJwtDTO);
        log.info("当前登录用户：{}", userJwtDTO);

        // 6.放行
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
