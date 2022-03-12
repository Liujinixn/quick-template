package com.quick.common.utils.flie;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word模板文件转PDF文件操作类
 *
 * @author Liujinxin
 */
public class WordPdfUtil {

    /**
     * 系统模板文件路径 (resources路径开始)
     */
    private static final String TEMPLATE_FILE_PATH = "template_file/";

    /**
     * 模板文件变量规则 例：${name}
     */
    private static final String VARIABLE_SUBSTITUTION_RULE = "\\$\\{(.+?)}";

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
     * <p>
     * 示例：
     * Map<String, Object> params = new HashMap<>();
     * params.put("name", "司马徽");
     * params.put("age", "30");
     * params.put("sex", "男");
     * byte[] bytes = WordPdfUtil.wordTemplateGeneratePdf(params, "test.docx");
     * response.getOutputStream().write(bytes);
     * WordPdfUtil.setResponseInfo(response,"打印.pdf");
     *
     * @param params       模板中的变量替换列表
     * @param templateName resources/template_file/ 路径下的模板文件路径（或文件名）
     * @return PDF文件字节数组
     * @throws IOException file parsing exception
     */
    public static byte[] wordTemplateGeneratePdf(Map<String, Object> params,
                                                 String templateName) throws IOException {
        // 读取本地Word模板文件
        ClassPathResource classPathResource = new ClassPathResource(TEMPLATE_FILE_PATH + templateName);
        InputStream source = classPathResource.getInputStream();
        XWPFDocument doc = new XWPFDocument(source);

        // 替换变量占位符 ${xxx}格式，如：${name}
        WordPdfUtil.replaceParams(doc, params);
        //替换表格里面的变量
        WordPdfUtil.replaceInTable(doc, params);

        // 转换为 PDF文件输出流
        ByteArrayOutputStream outputStream = pdfConverter(doc);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = source.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        FileCommonlyUsedUtil.close(outputStream);
        FileCommonlyUsedUtil.close(source);
        return outputStream.toByteArray();
    }

    /**
     * 将 XWPFDocument 对象转 ByteArrayOutputStream
     *
     * @param doc word文档对象
     */
    private static ByteArrayOutputStream pdfConverter(XWPFDocument doc) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileCommonlyUsedUtil.createPath(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH);
        UUID tempFileName = UUID.randomUUID();
        try {
            // 生成临时文件
            File file = new File(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME);
            FileOutputStream tmpFIle = new FileOutputStream(file);
            doc.write(tmpFIle);

            // 将临时文件转pdf文件
            Doc2Pdf.wordToPdf(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME, FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);

            // 获取生成的pdf文件
            FileInputStream fis = new FileInputStream(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileCommonlyUsedUtil.deleteFile(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + WORD_FILE_FORMAT_SUFFIX_NAME);
            FileCommonlyUsedUtil.deleteFile(FileCommonlyUsedUtil.TEMPORARY_DIRECTORY_PATH + tempFileName + PDF_FILE_FORMAT_SUFFIX_NAME);
        }
        return byteArrayOutputStream;
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
            e.printStackTrace();
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    private static void replaceParams(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph paragraph;
        while (iterator.hasNext()) {
            paragraph = iterator.next();
            replaceParam(paragraph, params);
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param paragraph 要替换的段落
     * @param params    参数
     */
    private static void replaceParam(XWPFParagraph paragraph, Map<String, Object> params) {
        List<XWPFRun> runs;
        Matcher matcher;
        StringBuilder runText = new StringBuilder();

        if (matcher(paragraph.getParagraphText()).find()) {
            runs = paragraph.getRuns();
            int j = runs.size();
            for (int i = 0; i < j; i++) {
                runText.append(runs.get(0).toString());
                //保留最后一个段落，在这段落中替换值，保留段落样式
                if ((j - 1) == i) {
                    continue;
                }
                paragraph.removeRun(0);
            }
            matcher = matcher(runText.toString());
            if (matcher.find()) {
                while ((matcher = matcher(runText.toString())).find()) {
                    runText = new StringBuilder(matcher.replaceFirst(String.valueOf(params.get(matcher.group(1)))));
                }
                runs.get(0).setText(runText.toString(), 0);
            }
        }

    }

    /**
     * 替换表格里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    private static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        WordPdfUtil.replaceParam(para, params);
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     *
     * @param str 验证的数据内容，检查数据内容是否符合 VARIABLE_SUBSTITUTION_RULE 规则
     * @return Matcher对象
     */
    private static Matcher matcher(String str) {
        Pattern pattern = Pattern.compile(VARIABLE_SUBSTITUTION_RULE, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }

}


