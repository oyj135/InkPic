package com.yj.inkpic.utils;

import com.yj.inkpic.model.dto.UserJwtDTO;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * ThreadLocal工具类
 */
public class BaseContext {

    // 当前登录用户
    private static final ThreadLocal<UserJwtDTO> CURRENT_USER = new ThreadLocal<>();

    private BaseContext() {

    }

    public static void setCurrentUser(UserJwtDTO user) {
        CURRENT_USER.set(user);
    }

    public static UserJwtDTO getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void removeCurrentUser() {
        CURRENT_USER.remove();
    }

}
