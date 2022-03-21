package com.quick.common.utils.flie.dto;

/**
 * Word 模板参数替换入参 图片对象
 *
 * @author Liujinxin
 */
public class ImagesAttr {

    private final String src;

    private int w = 110;

    private int h = 130;

    /**
     * 初始化对象
     *
     * @param src 图片地址
     */
    public ImagesAttr(String src) {
        this.src = src;
    }

    /**
     * 设置图片属性
     *
     * @param src 图片地址
     * @param w   图片宽度
     * @param h   图片高度
     */
    public ImagesAttr(String src, int w, int h) {
        this.src = src;
        this.w = w;
        this.h = h;
    }

    public String getSrc() {
        return src;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
