package com.quick.log.config.params;

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
        prefix = RequestPrefixLogParams.REQUEST_PREFIX
)
public class RequestPrefixLogParams {

    public static final String REQUEST_PREFIX = "request.prefix";

    /**
     * log服务前缀
     */
    private String logServer;
}

