package com.yj.inkpic.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/15
 */
@SpringBootTest
public class JwtTest {

    @Value("${inkPic.jwt.admin-secret-key}")
    String key;

    @Value("${inkPic.jwt.admin-ttl}")
    Long data;


    @Test
    public void testJwt() {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", 1);
        dataMap.put("userAccount", "admin");

        // 生成token
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, key)
                .addClaims(dataMap) // 添加自定义信息
                .setExpiration(new Date(data)) // 设置过期时间
                .compact();// 生成token

        System.out.println(token);

    }
}
