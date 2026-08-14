package com.yj.inkpic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

// todo 如需开启 Redis，需移除 exclude 中的内容
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@MapperScan("com.yj.inkpic.mapper")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class InkPicApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkPicApplication.class, args);
    }

}
