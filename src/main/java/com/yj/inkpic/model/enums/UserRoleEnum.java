package com.yj.inkpic.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin"),
    BAN("被封号", "ban");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    // 静态 Map 缓存
    private static final Map<String, UserRoleEnum> VALUE_MAP = new HashMap<>();

    // 静态初始化
    static {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            VALUE_MAP.put(userRoleEnum.value, userRoleEnum);
        }
    }

    /**
     * 获取值列表
     *
     */
    public static final List<String> GET_VALUES = Arrays.stream(values())
            .map(item -> item.value)
            .collect(Collectors.toList());

    /**
     * 根据 value 获取枚举值
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
