package com.quick.file.service.impl;

import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import org.springframework.stereotype.Service;

/**
 * 本地存储
 *
 * @author Liujinxin
 */
@Service("localClientService")
public class LocalClientServiceImpl implements FileStoreService {

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        return null;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        return new byte[0];
    }

    @Override
    public String getAccessUrl(String fileName) {
        return null;
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        return null;
    }

    @Override
    public boolean deleteFile(String... fileName) {
        return false;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        return false;
    }
}
