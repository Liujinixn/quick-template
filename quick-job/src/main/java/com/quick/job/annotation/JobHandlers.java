package com.quick.job.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 描述当前类为 JobHandler 处理程序
 *
 * @author liujinxin
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface JobHandlers {

}
