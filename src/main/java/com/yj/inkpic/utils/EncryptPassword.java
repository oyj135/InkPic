package com.yj.inkpic.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 密码加密
 */
public class EncryptPassword {
    // 随机盐值
    private static final String SALT = "ink_pic";

    public static String getEncryptPassword(String password) {
        return SALT + "@" + DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
    }

}
