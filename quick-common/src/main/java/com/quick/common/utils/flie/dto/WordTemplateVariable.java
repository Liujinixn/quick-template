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
     * 文本变量替换
     */
    private Map<String, Object> textParams = new HashMap<>(4);

    /**
     * 图片变量替换
     */
    private Map<String, ImagesAttr> imageParams = new HashMap<>(4);

    private WordTemplateVariable() {
    }

    private WordTemplateVariable(String templateName) {
        this.templateName = templateName;
    }

    public WordTemplateVariable(String templateName, Map<String, Object> textParams, Map<String, ImagesAttr> imageParams) {
        this.templateName = templateName;
        this.textParams = textParams;
        this.imageParams = imageParams;
    }

    /**
     * 创建模板参数对象
     */
    public static WordTemplateVariable createTemplateParams() {
        return new WordTemplateVariable();
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
}
