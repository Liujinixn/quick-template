package com.quick.log.config.params;

import com.quick.log.config.params.internal.RecordSpecificPathInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public static final String LOGBACK_CORE_PARAMETER_PREFIX = "log";

    /**
     * 是否开启服务日志记录 (true:将需要鉴权的接口进行操作记录到日志表)
     */
    private boolean enable;

    /**
     * 是否允许通过API接口访问logback服务html日志文件（不安全的操作） 即接口是否对外开放：/logback/{type}/{dateTime}
     */
    private boolean accessLogFileEnable;

    /**
     * enable为true时，系统会将鉴权的接口进行日志记录外，同时可以配置特定的接口也进行日志记录
     */
    private List<RecordSpecificPathInfo> recordSpecificPathList = new ArrayList<>();

    /**
     * 日志文件保存路径
     */
    private String path;

    /**
     * 通过API接口查询日志文件密钥，多个参数使用逗号隔开 （get请求，携带参数 access_key=${access_key} 即可）
     */
    @Value("${log.accessKey}")
    private List<String> logAccessKeyList;

}
