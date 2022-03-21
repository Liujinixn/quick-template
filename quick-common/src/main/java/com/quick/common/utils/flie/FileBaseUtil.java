package com.quick.common.utils.flie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * 文件常用操作工具累
 *
 * @author Liujinxin
 */
public class FileBaseUtil {

    private static final Logger log = LoggerFactory.getLogger(FileBaseUtil.class);

    /**
     * 临时目录
     */
    public static final String TEMPORARY_DIRECTORY_PATH = "/tmp/";

    /**
     * windows标识
     */
    private static final String WIN_SYMBOL = "win";

    /**
     * 关闭输入流
     *
     * @param is 输入流
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.warn("关闭输入流失败", e);
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os 输出流
     */
    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                log.warn("关闭输出流失败", e);
            }
        }
    }

    /**
     * 判断目录是否存在，不存在则插件
     *
     * @param path 目录地址
     */
    public static void createPath(String path) {
        String os = System.getProperty("os.name");
        String tmpPath = path;
        // 兼容 linux 系统
        if (os.toLowerCase().startsWith(WIN_SYMBOL)) {
            tmpPath = path.replaceAll("/", "\\\\");
        }
        // 判断路径是否存在
        File filePath = new File(tmpPath);
        if (!filePath.exists()) {
            // 创建目录
            filePath.mkdirs();
        }
    }

    /**
     * 删除文件，可以是单个文件或文件夹
     *
     * @param fileName 待删除的文件名
     * @return 文件删除成功返回true, 否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.warn("删除文件失败：{} 文件不存在", fileName);
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     * @return 单个文件删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dir 被删除目录的文件路径
     * @return 目录删除成功返回true 否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.warn("删除目录失败：{} 目录不存在", dir);
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            log.warn("删除目录失败", dir);
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            log.info("删除目录成功", dir);
            return true;
        } else {
            log.warn("删除目录失败", dir);
            return false;
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            // 删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            java.io.File myFilePath = new java.io.File(filePath);
            // 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            log.error("删除文件夹失败, folderPath = {}", folderPath, e);
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return 目录删除成功返回true 否则返回false
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                // 再删除空文件夹
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 将输入流转字节数组
     *
     * @param inStream 输入流
     * @return 字节数组
     */
    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //把outStream里的数据写入内存
        byte[] bytes = outStream.toByteArray();
        close(inStream);
        close(outStream);
        return bytes;
    }

    /**
     * 将url地址指向的图片，保存到临时文件目录（图片格式强制为jpg）
     *
     * @param urlAddress url地址
     * @return 本地临时文件文件地址
     */
    public static String downloadWebImages(String urlAddress) {
        String fileName;
        InputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            URL url = new URL(urlAddress);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(10 * 1000);
            //通过输入流获取图片数据
            inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inStream);

            // 生成文件名称
            fileName = UUID.randomUUID().toString();
            // 设置所有图片文件强制为jpg格式
            fileName = fileName + ".jpg";
            File imageFile = new File(TEMPORARY_DIRECTORY_PATH + fileName);
            //创建输出流
            outStream = new FileOutputStream(imageFile);
            //写入数据
            outStream.write(data);
        } catch (Exception e) {
            log.error("{} 图片地址，保存到临时文件目录失败", urlAddress, e);
            return null;
        } finally {
            close(outStream);
            close(inStream);
        }
        return TEMPORARY_DIRECTORY_PATH + fileName;
    }

}
