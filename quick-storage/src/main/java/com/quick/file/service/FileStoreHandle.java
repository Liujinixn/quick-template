package com.quick.file.service;

import com.quick.file.enumerate.FileSuffixTypeEnum;

/**
 * 文件处理 Service
 *
 * @author Liujinxin
 */
public interface FileStoreHandle {

    /**
     * 文件上传
     *
     * @param content            文件字节数组
     * @param fileSuffixTypeEnum 文件后缀类型枚举
     * @return 文件名称标识（可用于下载、获取文件访问地址、删除文件等情况，通过该标识定位文件）
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
    String getAccessUrl(String fileName);

    /**
     * 获取文件访问地址（有时效时间）
     *
     * @param fileName       文件标识
     * @param expirationTime 过期时间（单位S）
     * @return 访问地址（存储方式为 "local" 类型时，失效时间设置无效）
     */
    String getAccessUrl(String fileName, Long expirationTime);

    /**
     * 删除文件
     *
     * @param fileName 文件标识
     * @return true成功 | false失败
     */
    boolean deleteFile(String... fileName);

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件标识
     * @return true存在 | false不存在
     */
    boolean doesFileExist(String fileName);
}
