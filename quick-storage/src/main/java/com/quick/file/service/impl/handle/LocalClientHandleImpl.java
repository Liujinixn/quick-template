package com.quick.file.service.impl.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreHandle;
import com.quick.file.utils.local.FTPUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * FTP 存储
 *
 * @author Liujinxin
 */
@Slf4j
public class LocalClientHandleImpl implements FileStoreHandle {

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        String now = DateUtil.format(new Date(), "yyyy/MM/dd");
        String fileName = IdUtil.simpleUUID() + fileSuffixTypeEnum.getDescription();

        boolean result;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData(localStorageCoreParameters);
        try {
            result = ftpUtil.uploadFile(now, fileName, new ByteArrayInputStream(content));
        } finally {
            ftpUtil.ftpLogout();
        }
        return result ? now + "/" + fileName : null;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);

        byte[] bytes;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData(localStorageCoreParameters);
        try {
            bytes = ftpUtil.downloadFile(remotePath, fileName);
        } finally {
            ftpUtil.ftpLogout();
        }
        return bytes;
    }

    @Override
    public String getAccessUrl(String fileName) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        return localStorageCoreParameters.getWebHost() + "/" + fileName;
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        return localStorageCoreParameters.getWebHost() + "/" + fileName;
    }

    @Override
    public boolean deleteFile(String... fileName) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData(localStorageCoreParameters);
        for (String name : fileName) {
            int index = name.lastIndexOf("/");
            String remotePath = name.substring(0, index);
            name = name.substring(index + 1);
            try {
                ftpUtil.deleteFile(remotePath, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ftpUtil.ftpLogout();
        return true;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        LocalStorageCoreParameters localStorageCoreParameters = getLocalStorageCoreParameters();
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);

        boolean result;
        FTPUtil ftpUtil = FTPUtil.constructFtpPrepareData(localStorageCoreParameters);
        try {
            result = ftpUtil.doesFileExist(remotePath, fileName);
        } finally {
            ftpUtil.ftpLogout();
        }
        return result;
    }

    private LocalStorageCoreParameters getLocalStorageCoreParameters() {
        return SpringUtil.getBean("localStorageCoreParameters", LocalStorageCoreParameters.class);
    }

}
