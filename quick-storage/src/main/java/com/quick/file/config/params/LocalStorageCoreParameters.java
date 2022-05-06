package com.quick.file.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 本地存储 初始化核心参数
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(
        prefix = LocalStorageCoreParameters.LOCAL_PARAMETER_PREFIX
)
public class LocalStorageCoreParameters {

    public static final String LOCAL_PARAMETER_PREFIX = "storage.local";

    private String webHost;

    private String ftpHost;

    private int ftpPort;

    private String ftpUsername;

    private String ftpPassword;

    private String ftpBasePath;

    public void setFtpBasePath(String ftpBasePath) {
        if(!ftpBasePath.endsWith("/")){
            ftpBasePath = ftpBasePath + "/";
        }
        this.ftpBasePath = ftpBasePath;
    }
}
