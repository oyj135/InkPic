package com.yj.inkpic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author OuYJ
 * todo 如需开启 Redis，需移除 exclude 中的内容
 */
@SpringBootApplication()
@MapperScan("com.yj.inkpic.mapper")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableAsync
public class InkPicApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkPicApplication.class, args);
    }

}
