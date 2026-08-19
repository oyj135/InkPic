package com.yj.inkpic.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * jwt令牌工具类
 */
public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 生成符合 HS256 算法的 SecretKey
        SecretKey key = Jwts.SIG.HS256.key().build();
        // 将自定义的字符串秘钥转换为算法需要的 SecretKey
        key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), key.getAlgorithm());

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                .claims(claims) // 设置自定义信息
                .signWith(key) // 设置签名使用的秘钥(算法会自动根据 key 推断)
                .expiration(exp); // 设置过期时间

        return builder.compact();
    }


    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 生成符合 HS256 算法的 SecretKey
        SecretKey key = Jwts.SIG.HS256.key().build();
        key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), key.getAlgorithm());

        // 得到DefaultJwtParser
        // 获取 Payload (即原来的 body)
        return Jwts.parser()
                .verifyWith(key) // 设置验签使用的秘钥
                .build()
                .parseSignedClaims(token) // 解析 JWT
                .getPayload();
    }


}
