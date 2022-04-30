package com.quick.file.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import com.quick.file.config.params.OssStorageCoreParameters;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import com.quick.file.utils.oss.STSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 阿里 OSS云存储
 *
 * @author Liujinxin
 */
@Slf4j
@Service("ossClientService")
public class OssClientServiceImpl implements FileStoreService {

    @Autowired
    OssStorageCoreParameters ossStorageCoreParameters;

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        String now = DateUtil.format(new Date(), "yyyy/MM/dd");
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String fileName = now + IdUtil.simpleUUID() + fileSuffixTypeEnum.getDescription();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                ossStorageCoreParameters.getEndpoint(),
                ossStorageCoreParameters.getAccessKeyId(),
                ossStorageCoreParameters.getAccessKeySecret());
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossStorageCoreParameters.getBucket(), fileName, new ByteArrayInputStream(content));
            // 指定上传文件操作时是否覆盖同名Object。
            // 不指定x-oss-forbid-overwrite时，默认覆盖同名Object。
            // 指定x-oss-forbid-overwrite为false时，表示允许覆盖同名Object。
            // 指定x-oss-forbid-overwrite为true时，表示禁止覆盖同名Object，如果同名Object已存在，程序将报错。
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader("x-oss-forbid-overwrite", "true");
            putObjectRequest.setMetadata(metadata);
            // 上传文件。
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return fileName;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        byte[] bytes = null;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                ossStorageCoreParameters.getEndpoint(),
                ossStorageCoreParameters.getAccessKeyId(),
                ossStorageCoreParameters.getAccessKeySecret());
        try {
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = ossClient.getObject(ossStorageCoreParameters.getBucket(), fileName);
            bytes = IOUtils.readStreamAsByteArray(ossObject.getObjectContent());

        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return bytes;
    }

    @Override
    public String getAccessUrl(String fileName) {
        String url = "https://%s.%s/%s";
        return String.format(url, ossStorageCoreParameters.getBucket(), ossStorageCoreParameters.getEndpoint(), fileName);
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        STSUtils stsUtils = STSUtils.constructStsPrepareData().assumeRole();
        String accessUrl = null;
        // 从STS服务获取临时访问凭证后，您可以通过临时访问密钥和安全令牌生成OSSClient。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                ossStorageCoreParameters.getEndpoint(),
                stsUtils.getAccessKeyId(),
                stsUtils.getAccessKeySecret(),
                stsUtils.getSecurityToken());
        try {
            // 设置签名URL过期时间，默认为3600秒（1小时）。
            Date expiration = new Date(new Date().getTime() + (expirationTime == null ? 3600 * 1000 : expirationTime * 1000));
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL url = ossClient.generatePresignedUrl(ossStorageCoreParameters.getBucket(), fileName, expiration);
            accessUrl = url.toString();
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return accessUrl;
    }

    @Override
    public boolean deleteFile(String... fileName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                ossStorageCoreParameters.getEndpoint(),
                ossStorageCoreParameters.getAccessKeyId(),
                ossStorageCoreParameters.getAccessKeySecret());

        List<List<String>> partitionKeys = ListUtil.partition(Arrays.asList(fileName), 1000);
        for (List<String> keys : partitionKeys) {
            try {
                // 填写需要删除的多个文件完整路径
                DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(
                        new DeleteObjectsRequest(ossStorageCoreParameters.getBucket()).withKeys(keys).withEncodingType("url"));
                List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
                try {
                    List<String> deleteFileObjList = new ArrayList<>();
                    for (String obj : deletedObjects) {
                        String deleteObj = URLDecoder.decode(obj, "UTF-8");
                        deleteFileObjList.add(deleteObj);
                    }
                    log.info("file deletion: {}", JSON.toJSONString(deleteFileObjList));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } catch (OSSException oe) {
                log.error("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                log.error("Error Message:" + oe.getErrorMessage());
                log.error("Error Code:" + oe.getErrorCode());
                log.error("Request ID:" + oe.getRequestId());
                log.error("Host ID:" + oe.getHostId());
            } catch (ClientException ce) {
                log.error("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                log.error("Error Message:" + ce.getMessage());
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        return true;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        boolean res = false;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                ossStorageCoreParameters.getEndpoint(),
                ossStorageCoreParameters.getAccessKeyId(),
                ossStorageCoreParameters.getAccessKeySecret());

        try {
            // 判断文件是否存在。如果返回值为true，则文件存在，否则存储空间或者文件不存在。
            res = ossClient.doesObjectExist(ossStorageCoreParameters.getBucket(), fileName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return res;
    }
}
