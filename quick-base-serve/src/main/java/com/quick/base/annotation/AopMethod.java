package com.quick.base.annotation;

import java.lang.annotation.*;

/**
 * 自定义AOP切面 注解
 *
 * @author Liujinxin
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AopMethod {

    String value() default "";

}
