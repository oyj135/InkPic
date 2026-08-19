package com.yj.inkpic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/18
 * 操作日志注解
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /**
     * 操作模块
     * @return
     */
    String module() default "";

    /**
     * 操作类型
     * @return
     */
    String type() default "";

}
