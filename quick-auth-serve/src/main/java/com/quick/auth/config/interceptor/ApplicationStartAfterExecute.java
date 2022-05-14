package com.quick.auth.config.interceptor;

import com.quick.auth.config.interceptor.handler.ValidationAuthServe;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 服务启动后执行程序
 *
 * @author Liujinxin
 */
@Configuration
public class ApplicationStartAfterExecute implements InitializingBean {

    @Autowired
    private ValidationAuthServe validationAuthServe;

    @Override
    public void afterPropertiesSet() {
        validationAuthServe.validationServeAccessKey();
    }
}

