package com.yj.inkpic.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录接口限流（防暴力破解 / 撞库）
 * 基于 Redis 原子计数：同一账号在窗口期内超过最大尝试次数则拒绝。
 *
 * @author OuYJ
 */
@Component
public class LoginRateLimiter {

    /**
     * 限流窗口时长（分钟）
     */
    private static final long WINDOW_MINUTES = 10;
    /**
     * 窗口内最大尝试次数
     */
    private static final long MAX_ATTEMPTS = 5;

    private final StringRedisTemplate stringRedisTemplate;

    public LoginRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public record RateLimitResult(
            boolean blocked,
            long remainingAttempts,
            long ttlSeconds) {
    }


    /**
     * 判断指定账号是否已被限流（超过最大尝试次数）
     *
     * @param userAccount 登录账号
     * @return true 表示已被限流，应直接拒绝登录
     */
    public RateLimitResult checkBlock(String userAccount) {
        if (userAccount == null) {
            return new RateLimitResult(false, 0, 0);
        }
        String key = buildKey(userAccount);
        String count = stringRedisTemplate.opsForValue().get(key);
        if (count == null) {
            return new RateLimitResult(false, 0, 0);
        }
        long result;
        try {
            result = Long.parseLong(count);
            if (result >= MAX_ATTEMPTS) {
                Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
                return new RateLimitResult(true, 0, expire);
            }
        } catch (NumberFormatException e) {
            return new RateLimitResult(false, 0, 0);
        }
        return new RateLimitResult(false, MAX_ATTEMPTS - result, 0);
    }

    /**
     * 记录一次登录尝试（无论成功失败都计数，失败越多越快触发限流）
     *
     * @param userAccount 登录账号
     */
    public void recordAttempt(String userAccount) {
        if (userAccount == null) {
            return;
        }
        String key = buildKey(userAccount);
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // 首次尝试设置过期时间，避免 key 永久堆积
            stringRedisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
        }
    }

    private String buildKey(String userAccount) {
        return "login:limit:" + userAccount;
    }
}
