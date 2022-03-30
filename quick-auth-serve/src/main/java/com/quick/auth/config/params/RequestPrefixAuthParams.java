package com.quick.auth.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * request请求前缀路径
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "request.prefix")
public class RequestPrefixAuthParams {

    /**
     * auth服务前缀
     */
    private String authServer;
}