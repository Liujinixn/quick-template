package com.quick.common.utils.file;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word模板文件转PDF文件操作类
 *
 * @author Liujinxin
 */
public class WordPdfUtil {

    /**
     * 系统模板文件路径
     */
    private static final String TEMPLATE_FILE_PATH = "template_file/";

    /**
     * 模板文件变量规则 例：${name}
     */
    private static final String VARIABLE_SUBSTITUTION_RULE = "\\$\\{(.+?)}";

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
        ClassPathResource classPathResource = new ClassPathResource(TEMPLATE_FILE_PATH + templateName);
        InputStream source = classPathResource.getInputStream();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfOptions options = PdfOptions.create();
        XWPFDocument doc = new XWPFDocument(source);

        // 替换变量占位符 ${xxx}格式，如：${name}
        WordPdfUtil.replaceParams(doc, params);
        //替换表格里面的变量
        WordPdfUtil.replaceInTable(doc, params);
        // 转换为pdf
        PdfConverter.getInstance().convert(doc, outputStream, options);

        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = source.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            // 忽略 java.io.IOException: Stream closed
        }
        close(outputStream);
        close(source);
        return outputStream.toByteArray();
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

    /**
     * 关闭输入流
     *
     * @param is 输入流
     */
    private static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os 输出流
     */
    private static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
