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
     * 本地存储根路径位置
     */
    private String path;

    /**
     * web容器存放文件地址 <协议>://<主机>:<端口>
     */
    private String url;

    public void setPath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        this.path = path;
    }

    public void setUrl(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        this.url = url;
    }
}
