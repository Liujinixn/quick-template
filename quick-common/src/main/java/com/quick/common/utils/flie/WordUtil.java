package com.quick.common.utils.flie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private static final Logger log = LoggerFactory.getLogger(WordUtil.class);

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
    private static final Map<String, Integer> DOCUMENT_TYPE_MAP = new HashMap<>();

    static {
        DOCUMENT_TYPE_MAP.put(".emf", XWPFDocument.PICTURE_TYPE_EMF);
        DOCUMENT_TYPE_MAP.put(".wmf", XWPFDocument.PICTURE_TYPE_WMF);
        DOCUMENT_TYPE_MAP.put(".pict", XWPFDocument.PICTURE_TYPE_PICT);
        DOCUMENT_TYPE_MAP.put(".jpeg", XWPFDocument.PICTURE_TYPE_JPEG);
        DOCUMENT_TYPE_MAP.put(".jpg", XWPFDocument.PICTURE_TYPE_JPEG);
        DOCUMENT_TYPE_MAP.put(".png", XWPFDocument.PICTURE_TYPE_PNG);
        DOCUMENT_TYPE_MAP.put(".dib", XWPFDocument.PICTURE_TYPE_DIB);
        DOCUMENT_TYPE_MAP.put(".gif", XWPFDocument.PICTURE_TYPE_GIF);
        DOCUMENT_TYPE_MAP.put(".tiff", XWPFDocument.PICTURE_TYPE_TIFF);
        DOCUMENT_TYPE_MAP.put(".eps", XWPFDocument.PICTURE_TYPE_EPS);
        DOCUMENT_TYPE_MAP.put(".bmp", XWPFDocument.PICTURE_TYPE_BMP);
        DOCUMENT_TYPE_MAP.put(".wpg", XWPFDocument.PICTURE_TYPE_WPG);
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
            replaceParam(paragraph, params, doc);
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
                        replaceParam(para, params, doc);
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
    private static void replaceParam(XWPFParagraph paragraph, Map<String, Object> params, XWPFDocument doc) {
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
            if (!matcher.find()) {
                return;
            }
            while ((matcher = matcher(runText.toString())).find()) {
                Object val = params.get(matcher.group(1));
                boolean res;
                switch (getObjectType(val)) {
                    case "TableAttr":
                        // 替换变量-> 自定义表格
                        res = true;
                        Map<String, Object> tableAttr = objToMap(val);
                        substitutionVariableTable(tableAttr, paragraph, runs, doc);
                        runText = new StringBuilder(matcher.replaceAll(""));
                        break;
                    case "ImagesAttr":
                        // 替换变量-> 图片
                        res = true;
                        Map<String, Object> imgAttr = objToMap(val);
                        substitutionVariablePicture(imgAttr, runs);
                        runText = new StringBuilder(matcher.replaceAll(""));
                        break;
                    default:
                        // 替换变量-> 文本
                        res = true;
                        runText = new StringBuilder(matcher.replaceAll(String.valueOf(val)));
                }
                if (!res) {
                    // 变量在模板中不存在
                    runText = new StringBuilder(matcher.replaceAll(""));
                }

            }
            runs.get(0).setText(runText.toString(), 0);
        }
    }

    /**
     * 替换目标变量为表格
     *
     * @param tableAttr 表格信息
     * @param runs      通用属性的文本区域
     * @return 将 matcher 对象中的 变量 替换为 空后的结果，使用该参数 继续后续的变量替换
     */
    private static void substitutionVariableTable(Map<String, Object> tableAttr, XWPFParagraph paragraph, List<XWPFRun> runs, XWPFDocument doc) {

        XmlCursor cursor = paragraph.getCTP().newCursor();
        // 在指定游标位置插入表格
        XWPFTable table = doc.insertNewTbl(cursor);
        table.setWidthType(TableWidthType.PCT);

        List<String> headerRow = (List) tableAttr.get("headRowNameList");
        List<JSONArray> bodyRow = (List<JSONArray>) tableAttr.get("rowList");

        boolean isFirstColumn = true;
        for (int i = 0; i < headerRow.size(); i++) {
            if (isFirstColumn) {
                isFirstColumn = false;
                XWPFTableCell cell = table.getRow(0).getCell(i);
                cell.setText(headerRow.get(i));
                setCellStyle(cell);
                continue;
            }
            XWPFTableCell cell = table.getRow(0).createCell();
            cell.setText(headerRow.get(i));
            setCellStyle(cell);
        }

        for (int i = 0; i < bodyRow.size(); i++) {
            XWPFTableRow row = table.createRow();
            JSONArray cells = bodyRow.get(i);
            for (int j = 0; j < cells.size(); j++) {
                row.getCell(j).setText(String.valueOf(cells.get(j)));
            }
        }
    }

    /**
     * 替换目标变量为图片
     *
     * @param infoImg 图片属性信息
     * @param runs    通用属性的文本区域
     * @return 将 matcher 对象中的 变量 替换为 空后的结果，使用该参数 继续后续的变量替换
     */
    private static void substitutionVariablePicture(Map<String, Object> infoImg, List<XWPFRun> runs) {
        String src = checkUrlIsWebsiteAddress(MapUtils.getString(infoImg, "src"));
        double width = MapUtils.getDouble(infoImg, "w");
        double height = MapUtils.getDouble(infoImg, "h");

        Integer documentType;
        if (StringUtils.isBlank(src)) {
            log.warn("未找到 {} 图片文件", src);
            return;
        }
        String suffix = src.substring(src.lastIndexOf("."));
        documentType = MapUtils.getInteger(DOCUMENT_TYPE_MAP, suffix);
        if (documentType == null) {
            log.warn("不支持的图片: imgUrl = {}. 可选格式 emf|wmf|pict|jpeg|jpg|png|dib|gif|tiff|eps|bmp|wpg", src);
            return;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(src);
            runs.get(0).addPicture(inputStream, documentType, src, Units.toEMU(width), Units.toEMU(height));
        } catch (FileNotFoundException e) {
            log.error("读取临时图片失败：{} 文件不存在", src, e);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            log.error("POI无法写入同时图片：{} 图片格式未知", src, e);
        } catch (IOException e) {
            log.error("读取临时图片失败：{} 图片无法写入word模板", src, e);
        } finally {
            FileBaseUtil.close(inputStream);
            FileBaseUtil.deleteFile(src);
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
     * 数据转 List<Map<String, Object>>列表
     *
     * @param object 目标对象
     * @return List<Map < String, Object>>对象，null则说明数据无法转成列表
     */
    private static Map<String, Object> objToMapList(Object object) {
        if (object == null) {
            return null;
        }

        return null;
    }

    /**
     * 使用java.lang.reflect进行转换
     *
     * @param object 目标对象
     * @return Map对象，null则说明数据无法转成集合
     */
    private static Map<String, Object> objToMap(Object object) {
        if (object == null) {
            return null;
        }
        Map<String, Object> parmas = null;
        try {
            String jsonString = JSON.toJSONString(object);
            parmas = JSON.parseObject(jsonString, Map.class);
        } catch (Exception e) {
            // 数据不是对象，无法转换成Map，object 可能是字符串等数据类型
        }
        return parmas;
    }

    /**
     * 判断 address 是不是 网络地址，如果是网络地址下载到本地
     *
     * @param address 地址|路径
     * @return 图片本地地址, 返回 null 失败
     */
    private static String checkUrlIsWebsiteAddress(String address) {
        boolean res = address.startsWith("http");
        if (!res) {
            return address;
        }
        return FileBaseUtil.downloadWebImages(address);
    }

    /**
     * 获取Object对象原型对象
     *
     * @param val 目标判定对象
     * @return 类名
     */
    private static String getObjectType(Object val) {
        if (val == null) {
            return "";
        }
        String objTypeName = val.getClass().getName();
        String typeName = objTypeName.substring(objTypeName.lastIndexOf(".") + 1);
        return typeName;
    }

    /**
     * 设置表格单元格内容水平居中
     *
     * @param cell 单元格
     */
    private static void setCellStyle(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        CTTcPr ctPr = cttc.addNewTcPr();
        ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
    }
}
