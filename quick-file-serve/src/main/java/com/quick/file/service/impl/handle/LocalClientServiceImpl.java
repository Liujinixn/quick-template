package com.quick.file.service.impl.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import com.quick.file.utils.local.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * 本地存储
 *
 * @author Liujinxin
 */
@Slf4j
@Service("localClientService")
public class LocalClientServiceImpl implements FileStoreService {

    @Autowired
    LocalStorageCoreParameters localStorageCoreParameters;

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        String now = DateUtil.format(new Date(), "yyyy/MM/dd");
        String fileName = IdUtil.simpleUUID() + fileSuffixTypeEnum.getDescription();
        boolean result = false;
        try {
            result = FTPUtil.constructFtpPrepareData().uploadFile(now, fileName, new ByteArrayInputStream(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ? now + "/" + fileName : null;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);
        return FTPUtil.constructFtpPrepareData().downloadFile(remotePath, fileName);
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
        for (String name : fileName) {
            int index = name.lastIndexOf("/");
            String remotePath = name.substring(0, index);
            name = name.substring(index + 1);
            FTPUtil.constructFtpPrepareData().deleteFile(remotePath, name);
        }
        return true;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        int index = fileName.lastIndexOf("/");
        String remotePath = fileName.substring(0, index);
        fileName = fileName.substring(index + 1);
        return FTPUtil.constructFtpPrepareData().doesFileExist(remotePath, fileName);
    }
}
