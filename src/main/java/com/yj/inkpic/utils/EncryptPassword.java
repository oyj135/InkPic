package com.yj.inkpic.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 密码加密
 */
public class EncryptPassword {

    /**
     * 对明文密码进行 BCrypt 加密（自带随机盐，结果中包含盐值）
     * @param password 明文密码
     * @return BCrypt 哈希串
     */
    public static String getEncryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 校验明文密码与 BCrypt 哈希是否匹配
     * @param password 明文密码
     * @param encrypted 数据库中存储的 BCrypt 哈希
     * @return 是否匹配
     */
    public static boolean matches(String password, String encrypted) {
        if (password == null || encrypted == null) {
            return false;
        }
        return BCrypt.checkpw(password, encrypted);
    }

}
