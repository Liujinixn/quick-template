package com.quick.auth.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * shiro 初始化核心参数
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = ShiroCoreParameters.SHIRO_CORE_PARAMETER_PREFIX
)
public class ShiroCoreParameters {

    public static final String SHIRO_CORE_PARAMETER_PREFIX = "shiro";

    /**
     * 是否开启权限认证功能
     */
    private boolean enable = true;

    /**
     * 令牌过期时间（单位：秒）
     */
    private int tokenExpirationTime = 3600;

    /**
     * 加密名称
     */
    private String hashAlgorithmName = "md5";

    /**
     * 哈希加密迭代次数
     */
    private int hashIterations = 2;

    /**
     * 同一个帐号最大会话数
     */
    private int maxSession = 1;

    /**
     * false顶掉当前登录的用户/true顶掉之前登录的用户
     */
    private Boolean kickoutAfter = false;

    /**
     * 令牌名称
     */
    private String tokenKey = "token";

    /**
     * token值前缀
     */
    private String tokenValuePrefix = "";

    /**
     * shiroRedis参数
     */
    private ShiroRedis shiroRedis = new ShiroRedis();

    /**
     * 设置指定路径，需要认证和鉴权
     */
    private List<String> excludeAuthPathList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ShiroRedis {

        private String host;

        private String password;

        /**
         * 设置redis使用的db库
         */
        private int database = 0;

        /**
         * 在线用户token令牌缓存前缀
         */
        private String prefixUserAuth = "system_name:shiro:session:";

        /**
         * 账号异地登录数量缓存前缀
         */
        private String prefixOnline = "system_name:shiro:online:";

        /**
         * 其它信息缓存前缀
         */
        private String prefixOther = "system_name:shiro:";
    }

}
