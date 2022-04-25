package com.quick.file.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS 初始化核心参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = OssStorageCoreParameters.OSS_PARAMETER_PREFIX
)
public class OssStorageCoreParameters {

    public static final String OSS_PARAMETER_PREFIX = "oss";

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucket;

    private String securityToken;
}
