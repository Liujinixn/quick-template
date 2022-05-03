package com.quick.file.service.impl.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import com.quick.file.utils.local.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * FTP 存储
 *
 * @author Liujinxin
 */
@Slf4j
public class LocalClientServiceImpl implements FileStoreService {

    @Autowired(required = false)
    LocalStorageCoreParameters localStorageCoreParameters;

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        String now = DateUtil.format(new Date(), "yyyy/MM/dd");
        String fileName = IdUtil.simpleUUID() + fileSuffixTypeEnum.getDescription();

        boolean result;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData();
        try {
            result = ftpUtil.uploadFile(now, fileName, new ByteArrayInputStream(content));
        } finally {
            ftpUtil.ftpLogout();
        }
        return result ? now + "/" + fileName : null;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);

        byte[] bytes;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData();
        try {
            bytes = ftpUtil.downloadFile(remotePath, fileName);
        } finally {
            ftpUtil.ftpLogout();
        }
        return bytes;
    }

    @Override
    public String getAccessUrl(String fileName) {
        return localStorageCoreParameters.getWebHost() + "/" + fileName;
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        return localStorageCoreParameters.getWebHost() + "/" + fileName;
    }

    @Override
    public boolean deleteFile(String... fileName) {
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData();
        for (String name : fileName) {
            int index = name.lastIndexOf("/");
            String remotePath = name.substring(0, index);
            name = name.substring(index + 1);
            try {
                FTPUtil.constructFtpPrepareData().deleteFile(remotePath, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ftpUtil.ftpLogout();
        return true;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);

        boolean result;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData();
        try {
            result = ftpUtil.doesFileExist(remotePath, fileName);
        } finally {
            ftpUtil.ftpLogout();
        }
        return result;
    }
}
