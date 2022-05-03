package com.quick.file.utils.local;

import cn.hutool.extra.spring.SpringUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FTP文件操作工具类
 *
 * @author Liujinxin
 */
@Slf4j
public class FTPUtil {

    private final LocalStorageCoreParameters localStorageCoreParameters;

    /**
     * FTP客户端
     */
    private final FTPClient ftp;


    /**
     * 私有构建方法
     *
     * @param localStorageCoreParameters FTP连接参数
     */
    private FTPUtil(LocalStorageCoreParameters localStorageCoreParameters) {
        this.localStorageCoreParameters = localStorageCoreParameters;
        this.ftp = new FTPClient();
        int reply;
        try {
            ftp.connect(localStorageCoreParameters.getFtpHost(), localStorageCoreParameters.getFtpPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(localStorageCoreParameters.getFtpUsername(), localStorageCoreParameters.getFtpPassword());// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                log.error("FTP连接失败");
            }
        } catch (IOException e) {
            log.error("FTP连接前出现出现异常", e);
        }
    }

    /**
     * 构建 FTP必要数据、创建FTPClient客户端对象，操作完后需要调用 ftpLogout()方法 断开连接
     *
     * @return STSUtils STS处理工具对象
     */
    public static FTPUtil constructFtpPrepareData() {
        return new FTPUtil(SpringUtil.getBean("localStorageCoreParameters", LocalStorageCoreParameters.class));
    }

    /**
     * 断开FTP连接
     */
    public void ftpLogout() {
        try {
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
    }

    /**
     * 上传文件
     *
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01
     * @param filename 上传到FTP服务器上的文件名
     * @param input    文件输入流
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        try {
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + filePath)) {
                //如果目录不存在创建目录
                String[] dirs = (localStorageCoreParameters.getFtpBasePath() + filePath).split("/");
                String tempPath = "";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            if (!ftp.storeFile(filename, input)) {
                return result;
            }
            input.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 下载文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @return 文件内容字节数组
     */
    public byte[] downloadFile(String remotePath,
                               String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean result = false;
        try {
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    ftp.retrieveFile(ff.getName(), byteArrayOutputStream);
                    result = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ? byteArrayOutputStream.toByteArray() : null;
    }

    /**
     * 删除文件
     *
     * @param remotePath 目标文件工作空间位置（即文件的路径）
     * @param fileName   要删除的文件名
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFile(String remotePath, String fileName) {
        boolean result = false;
        try {
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    ftp.dele(ff.getName());
                    result = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 文件是否存在
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   操作目标的文件名
     * @return 存在返回true，否则返回false
     */
    public boolean doesFileExist(String remotePath, String fileName) {
        boolean result = false;
        try {
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    result = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

