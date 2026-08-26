package com.yj.inkpic.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @author OUYJ
 */
public class RegexUtils {
    /**
     * 是否是无效手机格式
     * @param phone 要校验的手机号
     * @return true:不符合，false：符合
     */
    public static boolean isPhoneInvalid(String phone){
        return misMatch(phone, AbstractRegexPatterns.PHONE_REGEX);
    }
    /**
     * 是否是无效邮箱格式
     * @param email 要校验的邮箱
     * @return true:不符合，false：符合
     */
    public static boolean isEmailInvalid(String email){
        return misMatch(email, AbstractRegexPatterns.EMAIL_REGEX);
    }

    /**
     * 是否是无效验证码格式
     * @param code 要校验的验证码
     * @return true:不符合，false：符合
     */
    public static boolean isCodeInvalid(String code){
        return misMatch(code, AbstractRegexPatterns.VERIFY_CODE_REGEX);
    }

    /**
     * 校验是否不符合正则格式
     * @param str 要校验的字符串
     * @param regex 正则表达式
     * @return true:不符合，false：符合
     */
    private static boolean misMatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
