package com.quick.auth.config.mvc;

import com.quick.auth.config.filter.AuthErrorResponseInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Mvc拦截器规则配置
 *
 * @author Liujinxin
 */
@Configuration
public class MvcAppConfig implements WebMvcConfigurer {

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
        list.add("/tourist/noLogin");
        list.add("/tourist/noAuth");
        list.add("/tourist/kickout");
        //addInterceptor(添加自定义的拦截器)   addPathPatterns(要拦截的路径) excludePathPatterns(放心的路径)
        registry.addInterceptor(authErrorResponseInterceptor()).addPathPatterns(list);
    }
}
