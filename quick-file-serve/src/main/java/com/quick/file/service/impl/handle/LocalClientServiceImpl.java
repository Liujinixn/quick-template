package com.quick.file.service.impl.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
        String fileName = now + "/" + IdUtil.simpleUUID() + fileSuffixTypeEnum.getDescription();

        FileWriter fileWriter = new FileWriter(localStorageCoreParameters.getPath() + fileName);
        fileWriter.write(content, 0, content.length);
        return fileName;
    }

    @Override
    public byte[] downloadStream(String fileName) {
        FileReader fileReader = new FileReader(localStorageCoreParameters.getPath() + fileName);
        return fileReader.readBytes();
    }

    @Override
    public String getAccessUrl(String fileName) {
        return localStorageCoreParameters.getUrl() + fileName;
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        return localStorageCoreParameters.getUrl() + fileName;
    }

    @Override
    public boolean deleteFile(String... fileName) {
        for (String name : fileName) {
            return FileUtil.del(localStorageCoreParameters.getPath() + name);
        }
        return true;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        return new File(localStorageCoreParameters.getPath() + fileName).exists();
    }
}
