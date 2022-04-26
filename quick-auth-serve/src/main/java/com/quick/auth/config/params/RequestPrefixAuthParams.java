package com.quick.auth.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * request请求前缀路径
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = RequestPrefixAuthParams.REQUEST_PREFIX
)
public class RequestPrefixAuthParams {

    public static final String REQUEST_PREFIX = "request.prefix";

    /**
     * auth服务前缀
     */
    private String authServer;
}
