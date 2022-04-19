package com.quick.auth.config.interceptor;

import com.quick.auth.config.interceptor.handler.AuthErrorResponseInterceptor;
import com.quick.auth.config.params.RequestPrefixAuthParams;
import com.quick.auth.config.params.ShiroCoreParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Mvc拦截器规则配置-争对权限认证失败的访问
 *
 * @author Liujinxin
 */
@Configuration
public class MvcAuthorizationFailedConfig implements WebMvcConfigurer {

    @Autowired
    RequestPrefixAuthParams requestPrefixAuthParams;

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    /**
     * 注入拦截器到bean
     */
    @Bean
    public AuthErrorResponseInterceptor authErrorResponseInterceptor() {
        return new AuthErrorResponseInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> list = new ArrayList<>();
        list.add(shiroCoreParameters.getNoLogin());
        list.add(shiroCoreParameters.getNoAuth());
        list.add(shiroCoreParameters.getKickOut());
        //addInterceptor(添加自定义的拦截器)   addPathPatterns(要拦截的路径) excludePathPatterns(放心的路径)
        registry.addInterceptor(authErrorResponseInterceptor()).addPathPatterns(list);
    }
}
