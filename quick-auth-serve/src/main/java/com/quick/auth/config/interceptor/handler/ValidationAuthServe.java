package com.quick.auth.config.interceptor.handler;

import com.quick.auth.config.params.AuthCoreParameters;
import com.quick.auth.entity.Serve;
import com.quick.auth.mapper.ServeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Auth服务认证
 *
 * @author Liujinxin
 */
@Slf4j
@Component
public class ValidationAuthServe {

    @Autowired
    private ServeMapper serveMapper;

    @Autowired
    private AuthCoreParameters authCoreParameters;

    @Autowired
    private ApplicationContext context;

    /**
     * 验证服务访问密钥
     */
    public void validationServeAccessKey() {
        log.info("---------------auth服务认证---------------");
        Serve serveInfoByServeName = serveMapper.findServeInfoByServeName(authCoreParameters.getServeName());
        if (null == serveInfoByServeName) {
            log.error("auth服务不存在, serveName = {}", authCoreParameters.getServeName());
            System.exit(SpringApplication.exit(context, () -> 0));
        }
        if (StringUtils.isBlank(authCoreParameters.getAccessKey()) || !authCoreParameters.getAccessKey().equals(serveInfoByServeName.getAccessKey())) {
            log.error("auth服务密钥错误, serveName = {}", authCoreParameters.getServeName());
            System.exit(SpringApplication.exit(context, () -> 0));
        }
        log.info("auth服务验证通过");
    }
}
