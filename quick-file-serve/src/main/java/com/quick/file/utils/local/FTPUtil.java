package com.quick.file.utils.local;

import cn.hutool.extra.spring.SpringUtil;
import com.quick.file.config.params.LocalStorageCoreParameters;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ftp上传下载工具类
 * <p>Title: FtpUtil</p>
 */
public class FTPUtil {

    private final LocalStorageCoreParameters localStorageCoreParameters;

    private FTPUtil(LocalStorageCoreParameters localStorageCoreParameters) {
        this.localStorageCoreParameters = localStorageCoreParameters;
    }

    /**
     * 构建 FTP必要数据
     *
     * @return STSUtils STS处理工具对象
     */
    public static FTPUtil constructFtpPrepareData() {
        return new FTPUtil(SpringUtil.getBean("localStorageCoreParameters", LocalStorageCoreParameters.class));
    }

    /**
     * 文件是否存在
     *
     * @param remotePath
     * @param fileName
     * @return
     */
    public boolean doesFileExist(String remotePath, String fileName) {
        FTPClient ftp = new FTPClient();
        boolean result = false;
        try {
            int reply;
            ftp.connect(localStorageCoreParameters.getFtpHost(), localStorageCoreParameters.getFtpPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(localStorageCoreParameters.getFtpUsername(), localStorageCoreParameters.getFtpPassword());// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                // 连接失败FTP失败
                return false;
            }
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    result = true;
                    break;
                }
            }
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
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @return
     */
    public byte[] downloadFile(String remotePath,
                               String fileName) {
        FTPClient ftp = new FTPClient();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean result = false;
        try {
            int reply;
            ftp.connect(localStorageCoreParameters.getFtpHost(), localStorageCoreParameters.getFtpPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(localStorageCoreParameters.getFtpUsername(), localStorageCoreParameters.getFtpPassword());// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return null;
            }
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    ftp.retrieveFile(ff.getName(), byteArrayOutputStream);
                    result = true;
                    break;
                }
            }
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
        return result ? byteArrayOutputStream.toByteArray() : null;
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(localStorageCoreParameters.getFtpHost(), localStorageCoreParameters.getFtpPort());// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(localStorageCoreParameters.getFtpUsername(), localStorageCoreParameters.getFtpPassword());// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
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
            ftp.logout();
            result = true;
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
        return result;
    }

    public boolean deleteFile(String remotePath, String fileName) {
        FTPClient ftp = new FTPClient();
        boolean result = false;
        try {
            int reply;
            ftp.connect(localStorageCoreParameters.getFtpHost(), localStorageCoreParameters.getFtpPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(localStorageCoreParameters.getFtpUsername(), localStorageCoreParameters.getFtpPassword());// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            ftp.changeWorkingDirectory(localStorageCoreParameters.getFtpBasePath() + remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    ftp.dele(ff.getName());
                    result = true;
                    break;
                }
            }
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
        return result;
    }
}

