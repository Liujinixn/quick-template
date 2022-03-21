package com.quick.common.utils.flie.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Word转PDF 模板参数替换入参对象
 *
 * @author Liujinxin
 */
public class WordTemplateVariable {

    /**
     * 模板路径|文件名称 resources.template_file 目录为根目录
     */
    private String templateName;

    /**
     * 变量 -> 段落文本
     * 可替换表格|段落中的变量
     */
    private Map<String, Object> textParams = new HashMap<>(4);

    /**
     * 变量 -> 图片
     * 可替换表格|段落中的变量，图片可支持url在线图片地址或本地图片地址
     */
    private Map<String, ImagesAttr> imageParams = new HashMap<>(4);

    /**
     * 变量 -> 自定义表格
     * 将指定变量替换为 自定义内容的表格
     */
    private Map<String, TableAttr> tableParams = new HashMap<>(2);


    private WordTemplateVariable() {
    }

    private WordTemplateVariable(String templateName) {
        this.templateName = templateName;
    }

    public WordTemplateVariable(String templateName, Map<String, Object> textParams, Map<String, ImagesAttr> imageParams, Map<String, TableAttr> tableParams) {
        this.templateName = templateName;
        this.textParams = textParams;
        this.imageParams = imageParams;
        this.tableParams = tableParams;
    }

    /**
     * 创建模板参数对象
     */
    public static WordTemplateVariable createTemplateParams(String templateName) {
        return new WordTemplateVariable(templateName);
    }

    /**
     * 追加 文本变量替换信息
     *
     * @param key   变量
     * @param value 值
     */
    public void putTextParams(String key, Object value) {
        this.textParams.put(key, value);
    }

    /**
     * 追加 图片变量替换信息
     *
     * @param imagesAttr 图片属性
     */
    public void putImageParams(String key, ImagesAttr imagesAttr) {
        this.imageParams.put(key, imagesAttr);
    }

    /**
     * 追加 自定义表格变量替换信息
     *
     * @param tableAttr 自定义表格内容
     */
    public void putTableParams(String key, TableAttr tableAttr) {
        this.tableParams.put(key, tableAttr);
    }

    public Map<String, Object> getTextParams() {
        return textParams;
    }

    public void setTextParams(Map<String, Object> textParams) {
        this.textParams = textParams;
    }

    public Map<String, ImagesAttr> getImageParams() {
        return imageParams;
    }

    public void setImageParams(Map<String, ImagesAttr> imageParams) {
        this.imageParams = imageParams;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, TableAttr> getTableParams() {
        return tableParams;
    }

    public void setTableParams(Map<String, TableAttr> tableParams) {
        this.tableParams = tableParams;
    }
}
