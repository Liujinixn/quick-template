package com.quick.file.service;

import com.quick.file.enumerate.FileSuffixTypeEnum;

/**
 * 文件处理 Service
 *
 * @author Liujinxin
 */
public interface FileStoreService {

    /**
     * 文件上传
     *
     * @param content            文件字节数组
     * @param fileSuffixTypeEnum 文件后缀类型枚举
     * @return 文件名称标识（可用于下载、生成文件有时效的访问地址、删除文件等情况，通过该标识定位文件）
     */
    String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum);

    /**
     * 文件下载
     *
     * @param fileName 文件标识
     * @return 文件字节数组
     */
    byte[] downloadStream(String fileName);

    /**
     * 获取文件访问地址（无失效时间）
     *
     * @param fileName 文件标识
     * @return 访问地址
     */
    String getAccessUrlPermanent(String fileName);

    /**
     * 获取文件访问地址（有时效时间）
     *
     * @param fileName 文件标识
     * @return 访问地址
     */
    String getAccessUrl(String fileName);

}
