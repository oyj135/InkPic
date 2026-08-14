package com.yj.inkpic.utils;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * ThreadLocal工具类
 */
public class BaseContext {

    private static final ThreadLocal<Integer> CURRENT_USER_ID = new ThreadLocal<>();

    private BaseContext() {

    }

    public static void setCurrentUserId(Integer userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Integer getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void removeCurrentUserId() {
        CURRENT_USER_ID.remove();
    }
}
