package com.quick.common.utils.flie.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Word转PDF 模板参数替换入参对象
 *
 * @author Liujinxin
 */
public class WordToPdfTemplateParams {

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
    private List<ImagesAttr> imageParams = new ArrayList<>(4);

    private WordToPdfTemplateParams() {
    }

    private WordToPdfTemplateParams(String templateName) {
        this.templateName = templateName;
    }

    private WordToPdfTemplateParams(String templateName, Map<String, Object> textParams, List<ImagesAttr> imageParams) {
        this.templateName = templateName;
        this.textParams = textParams;
        this.imageParams = imageParams;
    }

    /**
     * 创建模板参数对象
     */
    public static WordToPdfTemplateParams createTemplateParams() {
        return new WordToPdfTemplateParams();
    }

    /**
     * 创建模板参数对象
     */
    public static WordToPdfTemplateParams createTemplateParams(String templateName) {
        return new WordToPdfTemplateParams(templateName);
    }

    /**
     * 追加 文本变量替换信息
     *
     * @param key   变量
     * @param value 值
     */
    public void putTextParams(String key, String value) {
        this.textParams.put(key, value);
    }

    /**
     * 追加 图片变量替换信息
     *
     * @param imagesAttr 图片属性
     */
    public void putImageParams(ImagesAttr imagesAttr) {
        this.imageParams.add(imagesAttr);
    }

    public Map<String, Object> getTextParams() {
        return textParams;
    }

    public void setTextParams(Map<String, Object> textParams) {
        this.textParams = textParams;
    }

    public List<ImagesAttr> getImageParams() {
        return imageParams;
    }

    public void setImageParams(List<ImagesAttr> imageParams) {
        this.imageParams = imageParams;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
