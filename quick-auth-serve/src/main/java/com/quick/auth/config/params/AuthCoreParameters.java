package com.quick.auth.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 认证服务 初始化核心参数
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = AuthCoreParameters.AUTH_CORE_PARAMETER_PREFIX
)
public class AuthCoreParameters {

    public static final String AUTH_CORE_PARAMETER_PREFIX = "auth";

    /**
     * 认证服务名称
     */
    private String serveName;

    /**
     * 服务密钥
     */
    private String accessKey;
}
