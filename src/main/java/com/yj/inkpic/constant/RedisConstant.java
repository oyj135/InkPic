package com.yj.inkpic.constant;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/26
 *
 * Redis 常量
 */
public interface RedisConstant {

    /**
     * 登录接口限流
     */
    String LOGIN_LIMIT_KEY = "login:limit:";

    /**
     * 登录验证码
     */
    String LOGIN_CODE_KEY = "login:code:";

    /**
     * 登录验证码过期时间（2分钟）
     */
    Long LOGIN_CODE_TTL = 2L;

    /**
     * 登录token
     */
    String LOGIN_TOKEN_KEY = "login:token:";

    /**
     * 登录用户信息
     */
    String LOGIN_USER_KEY = "login:user:";
}
