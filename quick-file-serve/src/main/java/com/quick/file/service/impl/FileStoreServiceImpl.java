package com.quick.file.service.impl;

import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
import com.quick.file.service.impl.handle.LocalClientServiceImpl;
import com.quick.file.service.impl.handle.OssClientServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件处理分配
 *
 * @author Liujinxin
 */
@Service("fileStoreService")
public class FileStoreServiceImpl implements FileStoreService {

    /**
     * 自定义存储方式处理集合
     */
    private final Map<String, FileStoreService> handleWayMap = new HashMap<>();

    /**
     * 存储方式
     */
    @Value("${storage.way:local}")
    private String way;

    @PostConstruct
    public void init() {
        handleWayMap.put("oss", new OssClientServiceImpl());
        handleWayMap.put("local", new LocalClientServiceImpl());
    }

    @Override
    public String uploadByByte(byte[] content, FileSuffixTypeEnum fileSuffixTypeEnum) {
        return handleWayMap.get(way).uploadByByte(content, fileSuffixTypeEnum);
    }

    @Override
    public byte[] downloadStream(String fileName) {
        return handleWayMap.get(way).downloadStream(fileName);
    }

    @Override
    public String getAccessUrl(String fileName) {
        return handleWayMap.get(way).getAccessUrl(fileName);
    }

    @Override
    public String getAccessUrl(String fileName, Long expirationTime) {
        return handleWayMap.get(way).getAccessUrl(fileName, expirationTime);
    }

    @Override
    public boolean deleteFile(String... fileName) {
        return handleWayMap.get(way).deleteFile(fileName);
    }

    @Override
    public boolean doesFileExist(String fileName) {
        return handleWayMap.get(way).doesFileExist(fileName);
    }
}
