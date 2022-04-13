package com.quick.log.config.interceptor;

import com.quick.auth.entity.Permission;
import com.quick.auth.service.PermissionService;
import com.quick.enumerate.PermissionTypeEnum;
import com.quick.log.config.interceptor.handler.ServerLogInterceptorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置日志拦截器
 *
 * @author Liujinxin
 */
@Configuration
public class MvcLogConfig implements WebMvcConfigurer {

    @Autowired
    PermissionService permissionService;

    @Autowired
    ServerLogInterceptorHandler serverLogInterceptorHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /** 日志记录拦截 **/
        List<Permission> allPermissionList = permissionService.findAllPermissionList();
        List<String> urlList = allPermissionList.stream()
                .filter(o -> o.getType().equals(PermissionTypeEnum.BUTTON.getCode()))
                .map(o -> o.getUrl())
                .collect(Collectors.toList());
        // addInterceptor(添加自定义的拦截器)   addPathPatterns(要拦截的路径) excludePathPatterns(放行的路径)
        registry.addInterceptor(serverLogInterceptorHandler).addPathPatterns(urlList);
    }

}
