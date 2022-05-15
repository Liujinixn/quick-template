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

    /**
     * 文件访问容器（用于生成文件访问地址）
     */
    private String webHost;

    /**
     * FTP主机地址
     */
    private String ftpHost;

    /**
     * FTP连接端口
     */
    private int ftpPort;

    /**
     * FTP连接账号
     */
    private String ftpUsername;

    /**
     * FTP连接密码
     */
    private String ftpPassword;

    /**
     * 存储基础路径
     */
    private String ftpBasePath;

    public void setFtpBasePath(String ftpBasePath) {
        if (!ftpBasePath.endsWith("/")) {
            ftpBasePath = ftpBasePath + "/";
        }
        this.ftpBasePath = ftpBasePath;
    }
}
