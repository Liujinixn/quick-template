package com.quick.log.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 日志服务配置核心参数
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = LogBackCoreParameters.LOGBACK_CORE_PARAMETER_PREFIX
)
public class LogBackCoreParameters {

    public static final String LOGBACK_CORE_PARAMETER_PREFIX = "log.file";

    /**
     * 是否允许通过API接口访问logback服务html日志文件（不安全的操作） 即接口是否对外开放：/logback/{type}/{dateTime}
     */
    private boolean enable;

    /**
     * 日志文件保存路径
     */
    private String path;

    /**
     * 通过API接口查询日志文件密钥，多个参数使用逗号隔开 （get请求，携带参数 access_key=${access_key} 即可）
     */
    @Value("${log.file.access_key}")
    private List<String> logAccessKeyList;

}
