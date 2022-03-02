package com.quick.auth.config.shiro;

import com.quick.auth.shiro.CustomSessionManager;
import com.quick.auth.shiro.ShiroCoreParameters;
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
 */
@Configuration
public class ShiroConfig {

    /**
     * ShrioFilterFactoryBean 过滤bean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager,
                                                            @Qualifier("shiroService") ShiroService shiroService,
                                                            @Qualifier("shiroCoreParameters") ShiroCoreParameters shiroCoreParameters) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
        filtersMap.put("kickout", kickoutSessionControlFilter(shiroCoreParameters));
        bean.setFilters(filtersMap);

        Map<String, String> filterChainDefinitionMap = shiroService.loadFilterChainDefinitions();
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        bean.setLoginUrl("/tourist/noLogin");        //没有登录
        bean.setUnauthorizedUrl("/tourist/noAuth");  //没有权限
        return bean;
    }

    /**
     * 安全对象  DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm,
                                                                  @Qualifier("shiroCoreParameters") ShiroCoreParameters shiroCoreParameters) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager(shiroCoreParameters));  // 安全管理器中 设置 sessionManager
        securityManager.setCacheManager(cacheManager(shiroCoreParameters));
        return securityManager;
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
    public HashedCredentialsMatcher hashedCredentialsMatcher(@Qualifier("shiroCoreParameters") ShiroCoreParameters shiroCoreParameters) {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(shiroCoreParameters.getHashAlgorithmName());
        hashedCredentialsMatcher.setHashIterations(shiroCoreParameters.getHashIterations());
        return hashedCredentialsMatcher;
    }

    /**
     * 会话管理器
     */
    @Bean
    public DefaultWebSessionManager sessionManager(ShiroCoreParameters shiroCoreParameters) {
        CustomSessionManager sessionManager = new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO(shiroCoreParameters));
        // sessionManager.setSessionIdCookieEnabled(false);//禁用cookie 如果禁用会影响其他地方使用cookie如Durid监控
        sessionManager.setSessionIdUrlRewritingEnabled(false);//禁用url重写   url;jsessionid=id
        return sessionManager;
    }

    /**
     * 配置redisManager
     */
    public RedisManager getRedisManager(ShiroCoreParameters shiroCoreParameters) {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(shiroCoreParameters.getShiroRedis().getHost());
        redisManager.setPassword(shiroCoreParameters.getShiroRedis().getPassword());
        redisManager.setDatabase(shiroCoreParameters.getShiroRedis().getDatabase());
        return redisManager;
    }

    /**
     * 配置具体cache实现类
     */
    @Bean
    public RedisCacheManager cacheManager(ShiroCoreParameters shiroCoreParameters) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(getRedisManager(shiroCoreParameters));
        //设置redis过期时间，单位是秒
        redisCacheManager.setExpire(shiroCoreParameters.getTokenExpirationTime());
        redisCacheManager.setKeyPrefix(shiroCoreParameters.getShiroRedis().getPrefixOther()); //设置权限信息缓存的名称前缀
        return redisCacheManager;
    }

    /**
     * 自定义session持久化
     */
    @Bean
    public RedisSessionDAO redisSessionDAO(ShiroCoreParameters shiroCoreParameters) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(getRedisManager(shiroCoreParameters));
        redisSessionDAO.setExpire(shiroCoreParameters.getTokenExpirationTime());
        redisSessionDAO.setKeyPrefix(shiroCoreParameters.getShiroRedis().getPrefixUserAuth()); //设置session缓存的名称前缀
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
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
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
     * 限制同一账号登录同时登录人数控制
     */
    public KickoutSessionControlFilter kickoutSessionControlFilter(ShiroCoreParameters shiroCoreParameters) {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        //这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
        //也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
        kickoutSessionControlFilter.setCacheManager(cacheManager(shiroCoreParameters));
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(sessionManager(shiroCoreParameters));
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
        kickoutSessionControlFilter.setKickoutAfter(shiroCoreParameters.getKickoutAfter());
        //同一个用户最大的会话数，默认5；比如5的意思是同一个用户允许最多同时五个人登录；
        kickoutSessionControlFilter.setMaxSession(shiroCoreParameters.getMaxSession());
        // 设置缓存在线人数的key前缀
        kickoutSessionControlFilter.setOnlineUser(shiroCoreParameters.getShiroRedis().getPrefixOnline());
        //被踢出后重定向到的地址；
        kickoutSessionControlFilter.setKickoutUrl("/tourist/kickout");
        return kickoutSessionControlFilter;
    }

}
