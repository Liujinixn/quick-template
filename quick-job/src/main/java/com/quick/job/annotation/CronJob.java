package com.quick.job.annotation;

import java.lang.annotation.*;

/**
 * job自动注入xxl-job服务（需要配置 @JobHandlers 标识当前类）
 *
 * @author Liujinxin
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CronJob {

    String cron() default "";

    String name() default ""; // 跟这个注解里面值一样 @XxlJob("")

    String description() default "";

}
