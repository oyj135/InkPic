package com.yj.inkpic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt 令牌参数配置
 */
@Component
@ConfigurationProperties(prefix = "ink-pic.jwt")
@Data
public class JwtProperties {

    /**
     * 用户登录生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
