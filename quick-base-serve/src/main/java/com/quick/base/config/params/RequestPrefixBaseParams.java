package com.quick.base.config.params;

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
@ConfigurationProperties(prefix = "request.prefix")
public class RequestPrefixBaseParams {

    /**
     * base服务前缀
     */
    private String baseServer;
}

