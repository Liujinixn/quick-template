package com.quick.log.config.interceptor;

import com.quick.auth.entity.Permission;
import com.quick.auth.service.PermissionService;
import com.quick.common.enumerate.PermissionTypeEnum;
import com.quick.common.utils.redis.RedisClient;
import com.quick.log.config.interceptor.handler.ServerLogInterceptorHandler;
import com.quick.log.config.params.LogBackCoreParameters;
import com.quick.log.config.params.RequestPrefixLogParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
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

    @Autowired
    RequestPrefixLogParams requestPrefixLogParams;

    @Autowired
    LogBackCoreParameters logBackCoreParameters;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!logBackCoreParameters.isEnable()) {
            // 服务关闭日志记录
            return;
        }
        List<Permission> allPermissionList = permissionService.findAllPermissionList();

        // 拦截的接口列表
        List<String> urlList = allPermissionList.stream()
                .filter(o -> o.getType().equals(PermissionTypeEnum.BUTTON.getCode()))
                .map(o -> o.getUrl())
                .collect(Collectors.toList());

        // 拦截特定的接口列表
        urlList.addAll(logBackCoreParameters.getRecordSpecificPathList().stream().map(o -> o.getPath()).collect(Collectors.toList()));

        // 排除掉 查询操作日志列表的接口（不然数据会成倍积压）
        List<String> excludePathList = new ArrayList<>();
        excludePathList.add(requestPrefixLogParams.getLogServer() + "/operateLog/list");

        // addInterceptor(添加自定义的拦截器)   addPathPatterns(要拦截的路径) excludePathPatterns(放行的路径)
        registry.addInterceptor(serverLogInterceptorHandler).addPathPatterns(urlList).excludePathPatterns(excludePathList);
    }

}
