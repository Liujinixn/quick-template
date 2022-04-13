package com.quick.common.utils.flie;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.quick.common.utils.flie.dto.ImagesAttr;
import com.quick.common.utils.flie.dto.TableAttr;
import com.quick.common.utils.flie.dto.WordTemplateVariable;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Word模板文件转PDF文件操作类
 *
 * @author Liujinxin
 */
public class WordToPdfUtil {

    private static final Logger log = LoggerFactory.getLogger(WordToPdfUtil.class);

    /**
     * 模板文件格式后缀 | 模板转换后的临时word文件后缀名
     */
    private static final String WORD_FILE_FORMAT_SUFFIX_NAME = ".docx";

    /**
     * pdf文件后缀 | 临时文件后缀名
     */
    private static final String PDF_FILE_FORMAT_SUFFIX_NAME = ".pdf";

    /**
     * 根据指定的word模板文件，生成PDF文件
     *
     * @param templateParams Word模板参数替换对象
     * @return PDF文件字节数组
     * @throws IOException file parsing exception
     */
    public static byte[] wordTemplateGeneratePdf(WordTemplateVariable templateParams) throws IOException {
        // 获取模板文件 路径|文件名称
        String templateName = templateParams.getTemplateName();
        // 数据处理
        Map<String, Object> params = new HashMap<>(8);

        Map<String, Object> textParams = templateParams.getTextParams();
        if (!CollectionUtils.isEmpty(textParams)) {
            params.putAll(textParams);
        }

        Map<String, ImagesAttr> imageParams = templateParams.getImageParams();
        if (!CollectionUtils.isEmpty(imageParams)) {
            params.putAll(imageParams);
        }

        Map<String, TableAttr> tableParams = templateParams.getTableParams();
        if (!CollectionUtils.isEmpty(tableParams)) {
            params.putAll(tableParams);
        }

        return wordTemplateGeneratePdf(templateName, params);
    }

    /**
     * 设置响应头，响应内容为pdf文件
     *
     * @param response 响应头
     * @param fileName 下载的文件名称
     */
    public static void setResponseInfo(HttpServletResponse response, String fileName) {
        response.setContentType("application/pdf");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("不支持的编码 encode = {}", "UTF-8", e);
        }
    }

    /**
     * 根据指定的word模板文件，生成PDF文件
     * <p>
     * 示例：
     * Map<String, Object> params = new HashMap<>();
     * params.put("name", "司马徽");
     * params.put("age", "30");
     * params.put("sex", "男");
     * byte[] bytes = WordToPdfUtil.wordTemplateGeneratePdf(params, "test.docx");
     * response.getOutputStream().write(bytes);
     * WordToPdfUtil.setResponseInfo(response,"打印.pdf");
     *
     * @param templateName 模板路径|名称    resources.template_file 目录为根目录
     * @param params       模板中的变量替换列表
     *                     {
     *                     "name":"张三",                 // 替换模板文件中变量 为普通文本
     *                     "headPortrait":  {            // 替换模板文件中变量 为图片
     *                     "src": "http://img2.baidu.com/image",  // 图片地址,兼容本地磁盘和网络地址
     *                     "w": 110,                 // 宽度 px
     *                     "h": 130                  // 高度 px
     *                     },
     *                     "headPortrait":  {                                       // 替换模板文件中变量 为表格
     *                     "rowList": [["列1","列2","列3"],["lisi","29","男"]],  // 图片地址,兼容本地磁盘和网络地址
     *                     "rowHeightList": [30,30,30]                          // 每行行高
     *                     }
     *                     }
     * @return PDF文件字节数组
     * @throws IOException file parsing exception
     */
    private static byte[] wordTemplateGeneratePdf(String templateName, Map<String, Object> params) {
        // 读取本地Word模板文件
        ClassPathResource classPathResource = new ClassPathResource(WordUtil.TEMPLATE_FILE_PATH + templateName);
        InputStream source = null;
        ByteArrayOutputStream outputStream;
        XWPFDocument doc = null;
        try {
            source = classPathResource.getInputStream();
            doc = new XWPFDocument(source);
        } catch (IOException e) {
            log.error("读取|解析模板文件失败, wordTemplateFileName = {}", templateName, e);
        }
        // 替换变量占位符 ${xxx}格式，如：${name}
        WordUtil.replaceInParagraph(doc, params);
        //替换表格里面的变量
        WordUtil.replaceInTable(doc, params);

        // 转换为 PDF文件输出流
        outputStream = pdfConverter(doc);

        FileBaseUtil.close(outputStream);
        FileBaseUtil.close(source);
        return outputStream.toByteArray();
    }

    /**
     * 将Word文件的XWPFDocument对象，转PDF文件的ByteArrayOutputStream流
     *
     * @param doc word文档对象
     */
    private static ByteArrayOutputStream pdfConverter(XWPFDocument doc) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileBaseUtil.createPath(FileBaseUtil.TEMPORARY_DIRECTORY_PATH);
        UUID tempFileName = UUID.randomUUID();
        try {
            // 生成临时文件
            File file = new File(FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME);
            FileOutputStream tmpFile = new FileOutputStream(file);
            doc.write(tmpFile);
            FileBaseUtil.close(tmpFile);

            // 将临时文件转pdf文件
            wordToPdf(FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME, FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);

            // 获取生成的pdf文件
            FileInputStream fis = new FileInputStream(FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);
            FileChannel fisChannel = fis.getChannel();
            byteArrayOutputStream = new ByteArrayOutputStream((int) fisChannel.size());
            // 分配缓冲区，设定每次读的字节数
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            // 读取pdf数据
            int len;
            while ((len = fisChannel.read(byteBuffer)) > 0) {
                //上面把数据写入到了buffer，所以可知上面的buffer是写模式，调用flip把buffer切换到读模式，读取数据
                byteBuffer.flip();
                byteArrayOutputStream.write(byteBuffer.array(), 0, len);
                byteBuffer.clear();
            }
            FileBaseUtil.close(fis);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileBaseUtil.deleteFile(FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME);
            FileBaseUtil.deleteFile(FileBaseUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);
        }
        return byteArrayOutputStream;
    }

    /**
     * 加载license 用于破解 不生成水印
     */
    private static void getLicense() throws Exception {
        try (InputStream is = WordToPdfUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
            License license = new License();
            license.setLicense(is);
        }
    }

    /**
     * word 转 pdf
     * 说明：基于aspose-words-15.8.0-jdk16.jar 实现文件转换PDF文件
     * 示例：wordToPdf("D:\\Projects\\ideaProjects\\quick-template\\quick-config\\src\\main\\resources\\template_file\\test.docx",
     * "D:\\Projects\\ideaProjects\\quick-template\\quick-config\\src\\main\\resources\\template_file\\test.pdf");
     *
     * @param wordPath word文件保存的路径
     * @param pdfPath  转换后pdf文件保存的路径
     */
    private static void wordToPdf(String wordPath, String pdfPath) throws Exception {
        getLicense();
        File file = new File(pdfPath);
        try (FileOutputStream os = new FileOutputStream(file)) {
            com.aspose.words.Document doc = new Document(wordPath);
            doc.save(os, SaveFormat.PDF);
            FileBaseUtil.close(os);
        }
    }

}


