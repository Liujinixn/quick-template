package com.quick.base.config.aop;

import com.google.common.util.concurrent.RateLimiter;
import com.quick.base.annotation.AopMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopMethodHandler {

    /**
     * 争对包含 @AopMethod 注解的方法操作
     */
    @Before("@annotation(aopMethod)")
    public void doBefore(JoinPoint point, AopMethod aopMethod) throws Throwable {
        System.out.println(aopMethod.value());
        return ;
    }
}
