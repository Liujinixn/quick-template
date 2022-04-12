package com.quick.auth.shiro;

import com.quick.auth.config.params.RequestPrefixAuthParams;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.entity.Permission;
import com.quick.auth.service.PermissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shiro业务处理
 *
 * @author Liujinxin
 */
@Service
public class ShiroService {

    @Autowired
    PermissionService permissionService;

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    @Autowired
    RequestPrefixAuthParams requestPrefixAuthParams;

    /**
     * 初始化权限
     * shiro的内置过滤器:
     * anon 无需认证就可以访问
     * authc  必须认证才能访问
     * user  必须拥有记住我功能 才能用
     * perms  拥有对某个用户资源才能访问
     * role   拥有某个角色权限才能访问
     */
    public Map<String, String> loadFilterChainDefinitions() {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        if (!shiroCoreParameters.isEnable()) {
            // 系统禁用权限认证功能
            filterChainDefinitionMap.put("/**", "anon");
            return filterChainDefinitionMap;
        }
        filterChainDefinitionMap.put(requestPrefixAuthParams.getAuthServer() + "/tourist/**", "anon");
        //swagger/druid监控接口 权限开放
        filterChainDefinitionMap.put("*.html", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/v2/api-docs-ext", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/captcha.jpg", "anon");
        filterChainDefinitionMap.put("/csrf", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");

        // 排除指定的认证授权接口
        for (String path : shiroCoreParameters.getExcludeAuthPathList()) {
            filterChainDefinitionMap.put(path, "anon");
        }

        List<Permission> permissionList = permissionService.findAllPermissionList();
        for (Permission permission : permissionList) {
            if (StringUtils.isNotBlank(permission.getUrl()) && StringUtils.isNotBlank(permission.getPerms())) {
                String perm = "perms[" + permission.getPerms() + "]";
                filterChainDefinitionMap.put(permission.getUrl(), perm + ",kickout");
            }
        }

        filterChainDefinitionMap.put("/**", "user" + ",kickout");
        return filterChainDefinitionMap;
    }

}
