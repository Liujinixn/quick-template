package com.quick.common.utils.flie;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * word 工具类
 *
 * @author Liujinxin
 */
public class WordUtil {

    /**
     * 系统模板文件路径 (resources路径开始)
     */
    public static final String TEMPLATE_FILE_PATH = "template_file/";

    /**
     * 模板文件变量规则 例：${name}
     */
    private static final String VARIABLE_SUBSTITUTION_RULE = "\\$\\{(.+?)}";

    /**
     * 文件类型枚举
     */
    private static final Map<String, Integer> documentTypeMap = new HashMap<>();

    static {
        documentTypeMap.put(".emf", XWPFDocument.PICTURE_TYPE_EMF);
        documentTypeMap.put(".wmf", XWPFDocument.PICTURE_TYPE_WMF);
        documentTypeMap.put(".pict", XWPFDocument.PICTURE_TYPE_PICT);
        documentTypeMap.put(".jpeg", XWPFDocument.PICTURE_TYPE_JPEG);
        documentTypeMap.put(".jpg", XWPFDocument.PICTURE_TYPE_JPEG);
        documentTypeMap.put(".png", XWPFDocument.PICTURE_TYPE_PNG);
        documentTypeMap.put(".dib", XWPFDocument.PICTURE_TYPE_DIB);
        documentTypeMap.put(".gif", XWPFDocument.PICTURE_TYPE_GIF);
        documentTypeMap.put(".tiff", XWPFDocument.PICTURE_TYPE_TIFF);
        documentTypeMap.put(".eps", XWPFDocument.PICTURE_TYPE_EPS);
        documentTypeMap.put(".bmp", XWPFDocument.PICTURE_TYPE_BMP);
        documentTypeMap.put(".wpg", XWPFDocument.PICTURE_TYPE_WPG);
    }

    /**
     * 替换段落 里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    public static void replaceInParagraph(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph paragraph;
        while (iterator.hasNext()) {
            paragraph = iterator.next();
            replaceParam(paragraph, params);
        }
    }

    /**
     * 替换表格 里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    public static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
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
                        replaceParam(para, params);
                    }
                }
            }
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
                    String group = matcher.group(1);
                    Object val = params.get(group);
                    if (objToMap(val).get("table") != null) {
                        // 替换目标为 图片
                        Map<String, Object> infoImg = (Map<String, Object>) val;
                        String src = checkUrlIsWebsiteAddress(MapUtils.getString(infoImg, "src"));
                        double width = MapUtils.getDouble(infoImg, "w");
                        double height = MapUtils.getDouble(infoImg, "h");

                        String suffix = src.substring(src.lastIndexOf("."));
                        Integer documentType = MapUtils.getInteger(documentTypeMap, suffix);
                        if (documentType == null) {
                            System.err.println("Unsupported picture: " + src +
                                    ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
                            continue;
                        }
                        InputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(src);
                            runs.get(0).addPicture(inputStream, documentType, src, Units.toEMU(width), Units.toEMU(height));
                        } catch (InvalidFormatException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runs.get(0).addBreak(BreakType.PAGE);
                        val = "";
                        FileBaseUtil.close(inputStream);
                        FileBaseUtil.deleteFile(src);
                    }
                    // 替换目标为文本，如果为图片设置val = ""
                    String s = matcher.replaceAll(String.valueOf(val));
                    runText = new StringBuilder(s);

                }
                runs.get(0).setText(runText.toString(), 0);
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
     * 使用java.lang.reflect进行转换
     *
     * @param object 目标对象
     * @return map 转换Map结果，如果是Map结构的话会包含 table 参数
     */
    private static Map<String, Object> objToMap(Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (object == null) {
            return map;
        }
        try {
            Field[] declaredFields = object.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                try {
                    map.put(field.getName(), field.get(object));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 判断 address 是不是 网络地址，如果是网络地址下载到本地
     *
     * @param address 地址|路径
     * @return true 是
     */
    private static String checkUrlIsWebsiteAddress(String address) {
        boolean res = address.startsWith("http");
        if (!res) {
            return address;
        }
        return FileBaseUtil.downloadWebImages(address);
    }
}
