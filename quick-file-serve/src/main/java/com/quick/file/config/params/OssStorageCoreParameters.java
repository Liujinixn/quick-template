package com.quick.file.config.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS 初始化核心参数
 *
 * @author Liujinxin
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

    /**
     * 访问路径中的 Bucket
     * OSS创建访问路径时，会填写自定义地域、Bucket；如地域可选为北华北2（北京），那么 endpoint、regionId都会随之不同
     */
    private String bucket;

    /**
     * 外网Endpoint
     * 参考API（对象存储->用户指南->访问域名->访问域名和数据中心）: https://help.aliyun.com/document_detail/31837.htm?spm=a2c4g.11186623.0.0.1bab3385wE5dVK#concept-zt4-cvy-5db
     * 案例格式: oss-cn-beijing.aliyuncs.com
     * 创建访问路径时，选择的地域不同 外网Endpoint 随之不同
     */
    private String endpoint;

    /**
     * Region ID
     * 参考API（对象存储->用户指南->访问域名->访问域名和数据中心）: https://help.aliyun.com/document_detail/31837.htm?spm=a2c4g.11186623.0.0.1bab3385wE5dVK#concept-zt4-cvy-5db
     * 案例格式: oss-cn-beijing
     * 创建访问路径时，选择的地域不同 外网Endpoint 随之不同
     */
    private String regionId;

    /**
     * RAM访问控制 -> 用户 -> 用户AccessKey -> AccessKey ID
     */
    private String accessKeyId;

    /**
     * RAM访问控制 -> 用户 -> 用户AccessKey -> AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * RAM访问控制 -> 角色 -> ARN
     * 案例格式: acs:ram::1954506457702280:role/quick-file-serve
     */
    private String roleSessionName;

    /**
     * RAM访问控制 -> 角色 -> 角色名称
     */
    private String roleArn;

}
