package com.quick.file.utils.oss;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.quick.file.config.params.OssStorageCoreParameters;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里凭证STS工具类
 *
 * @author Liujinxin
 */
@Slf4j
public class STSUtil {

    private final OssStorageCoreParameters ossStorageCoreParameters;

    /**
     * STS权限返回实体
     */
    private AssumeRoleResponse assumeRoleResponse;

    private STSUtil(OssStorageCoreParameters ossStorageCoreParameters) {
        this.ossStorageCoreParameters = ossStorageCoreParameters;
    }

    /**
     * 构建 STS必要数据
     *
     * @return STSUtils STS处理工具对象
     */
    public static STSUtil constructStsPrepareData(OssStorageCoreParameters ossStorageCoreParameters) {
        return new STSUtil(ossStorageCoreParameters);
    }

    /**
     * 获取 STS私有凭证
     */
    public STSUtil assumeRole() {
        //构建一个阿里云客户端，用于发起请求。
        //构建阿里云客户端时需要设置AccessKey ID和AccessKey Secret。
        DefaultProfile profile = DefaultProfile.getProfile(
                ossStorageCoreParameters.getRegionId(),
                ossStorageCoreParameters.getAccessKeyId(),
                ossStorageCoreParameters.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        //构造请求，设置参数。关于参数含义和设置方法，请参见《API参考》。
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setSysRegionId(ossStorageCoreParameters.getRegionId());
        request.setRoleArn(ossStorageCoreParameters.getRoleArn());
        request.setRoleSessionName(ossStorageCoreParameters.getRoleSessionName());

        try {
            this.assumeRoleResponse = client.getAcsResponse(request);
        } catch (ServerException e) {
            log.error(e.getErrMsg(), e);
        } catch (ClientException e) {
            log.error("ErrCode:{} , ErrMsg:{}, RequestId:{}" + e.getErrCode(), e.getErrMsg(), e.getRequestId());
        }
        return this;
    }

    /**
     * 获取 SecurityToken 临时访问令牌
     */
    public String getSecurityToken() {
        return assumeRoleResponse.getCredentials().getSecurityToken();
    }

    /**
     * 获取 访问密钥 ID
     */
    public String getAccessKeyId() {
        return assumeRoleResponse.getCredentials().getAccessKeyId();
    }

    /**
     * 获取临时 访问密钥密钥
     */
    public String getAccessKeySecret() {
        return assumeRoleResponse.getCredentials().getAccessKeySecret();
    }

}

