package com.quick.base.config.aop;

import com.quick.base.annotation.AopMethod;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aop 方法处理程序
 */
@Slf4j
@Aspect
@Component
public class AopMethodHandler {

    /**
     * 争对包含 @AopMethod 注解的方法操作
     */
    @Before("@annotation(aopMethod)")
    public void doBefore(JoinPoint point, AopMethod aopMethod) throws Throwable {
        log.info(aopMethod.value());
        return;
    }
}
