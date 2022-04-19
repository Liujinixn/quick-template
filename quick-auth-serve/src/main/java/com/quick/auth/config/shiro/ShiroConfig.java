package com.quick.auth.config.shiro;

import cn.hutool.extra.spring.SpringUtil;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.shiro.CustomSessionManager;
import com.quick.auth.shiro.ShiroService;
import com.quick.auth.shiro.filter.KickoutSessionControlFilter;
import com.quick.auth.shiro.realm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置类
 *
 * @author Liujinxin
 */
@Configuration
@SuppressWarnings("all")
public class ShiroConfig {

    /**
     * 安全对象  DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 关联UserRealm
        securityManager.setRealm(userRealm);
        // 安全管理器中 设置 sessionManager
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    /**
     * ShrioFilterFactoryBean 过滤bean
     * <p>
     * 简写(加粗为常用)        名称        优先级(1为最高)           说明-对应Java类
     * anon                 匿名拦截器	    1               不需要登录就能访问,一般用于静态资源,或者移动端接口 - org.apache.shiro.web.filter.authc.AnonymousFilter
     * authc                登录拦截器	    2	            需要登录认证才能访问的资源	- org.apache.shiro.web.filter.authc.FormAuthenticationFilter
     * authcBasic           Http拦截器	    3               Http身份验证拦截器,非常用类型,不太了解 - org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
     * logout               登出拦截器	    4	            用户登出拦截器,主要属性:redirectURL退出登录后重定向的地址 - org.apache.shiro.web.filter.authc.LogoutFilter
     * noSessionCreation    不创建会话拦截器	5	            调用 subject.getSession(false) 不会有什么问题，但是如果 subject.getSession(true) 将抛出 DisabledSessionException 异常 - org.apache.shiro.web.filter.authc.NoSessionCreationFilter
     * prems	            权限拦截器	    6	            验证用户是否拥有资源权限 - org.apache.shiro.web.filter.authc.PermissionsAuthorizationFilter
     * port	                端口拦截器	    7	            其主要属性: port(80) 如果用户访问该页面是非 80，将自动将请求端口改为 80 并重定向到该 80 端口 - org.apache.shiro.web.filter.authc.PortFilter
     * rest	                rest风格拦截器	8	            rest 风格拦截器，自动根据请求方法构建权限字符串构建权限字符串；非常用类型拦截器 - org.apache.shiro.web.filter.authc.HttpMethodPermissionFilter
     * roles	            角色拦截器	    9	            验证用户是否拥有资源角色 - org.apache.shiro.web.filter.authc.RolesAuthorizationFilter
     * ssl	                SSL拦截器	    10	            只有请求协议是https才能通过,否则你会自动跳转到https端口(443) - org.apache.shiro.web.filter.authc.SslFilter
     * user	                用户拦截器	    11	            用户拦截器，用户已经身份验证 / 记住我登录的都可 - org.apache.shiro.web.filter.authc.UserFilter
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager,
                                                            @Qualifier("shiroService") ShiroService shiroService) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);
        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
        filtersMap.put("kickout", kickoutSessionControlFilter());
        bean.setFilters(filtersMap);

        Map<String, String> filterChainDefinitionMap = shiroService.loadFilterChainDefinitions();
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        // 没有登录重定向地址
        bean.setLoginUrl(shiroCoreParameters.getNoLogin());
        //没有权限重定向地址
        bean.setUnauthorizedUrl(shiroCoreParameters.getNoAuth());
        return bean;
    }

    /**
     * 创建 Realm对象 ，需要自定义Realm
     */
    @Bean
    public UserRealm userRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher hashedCredentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return userRealm;
    }

    /**
     * MD5加密
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(shiroCoreParameters.getHashAlgorithmName());
        hashedCredentialsMatcher.setHashIterations(shiroCoreParameters.getHashIterations());
        return hashedCredentialsMatcher;
    }

    /**
     * 会话管理器
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        CustomSessionManager sessionManager = new CustomSessionManager();
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        sessionManager.setSessionDAO(redisSessionDAO());
        // 禁用cookie 如果禁用会影响其他地方使用cookie如Durid监控
        // sessionManager.setSessionIdCookieEnabled(false);
        // 禁用url重写 url;jsessionid=id
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 配置redisManager
     */
    public RedisManager getRedisManager() {
        RedisManager redisManager = new RedisManager();
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        redisManager.setHost(shiroCoreParameters.getShiroRedis().getHost());
        redisManager.setPassword(shiroCoreParameters.getShiroRedis().getPassword());
        redisManager.setDatabase(shiroCoreParameters.getShiroRedis().getDatabase());
        return redisManager;
    }

    /**
     * 配置具体cache实现类
     */
    @Bean
    public RedisCacheManager cacheManager() {
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(getRedisManager());
        //设置redis过期时间，单位是秒
        redisCacheManager.setExpire(shiroCoreParameters.getTokenExpirationTime());
        //设置权限信息缓存的名称前缀
        redisCacheManager.setKeyPrefix(shiroCoreParameters.getShiroRedis().getPrefixOther());
        return redisCacheManager;
    }

    /**
     * 自定义session持久化
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(getRedisManager());
        redisSessionDAO.setExpire(shiroCoreParameters.getTokenExpirationTime());
        //设置session缓存的名称前缀
        redisSessionDAO.setKeyPrefix(shiroCoreParameters.getShiroRedis().getPrefixUserAuth());
        return redisSessionDAO;
    }

    /**
     * 管理shiro一些bean的生命周期 即bean初始化 与销毁
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 加入注解的使用，不加入这个AOP注解不生效(shiro的注解 例如 @RequiresGuest , 但是 一般使用配置文件的方式)
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new
                AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 用来扫描上下文寻找所有的Advistor(通知器), 将符合条件的Advisor应用到切入点的Bean中，需
     * 要在LifecycleBeanPostProcessor创建后才可以创建
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new
                DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 限制同一账号登录同时登录人数控制，自定义过滤规则
     */
    @Bean
    @DependsOn("shiroCoreParameters")
    public KickoutSessionControlFilter kickoutSessionControlFilter() {
        ShiroCoreParameters shiroCoreParameters = getShiroCoreParameters();
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        // 使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        // 这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
        // 也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
        kickoutSessionControlFilter.setCacheManager(cacheManager());
        // 用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(sessionManager());
        // 是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
        kickoutSessionControlFilter.setKickoutAfter(shiroCoreParameters.getKickoutAfter());
        // 同一个用户最大的会话数，默认5；比如5的意思是同一个用户允许最多同时五个人登录；
        kickoutSessionControlFilter.setMaxSession(shiroCoreParameters.getMaxSession());
        // 设置缓存在线人数的key前缀
        kickoutSessionControlFilter.setOnlineUser(shiroCoreParameters.getShiroRedis().getPrefixOnline());
        // 被踢出后重定向到的地址
        kickoutSessionControlFilter.setKickoutUrl(shiroCoreParameters.getKickOut());
        return kickoutSessionControlFilter;
    }

    private ShiroCoreParameters getShiroCoreParameters() {
        return SpringUtil.getBean("shiroCoreParameters", ShiroCoreParameters.class);
    }

}
