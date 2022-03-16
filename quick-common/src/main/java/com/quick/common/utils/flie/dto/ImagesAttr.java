package com.quick.common.utils.flie.dto;

/**
 * Word转PDF 模板参数替换入参 图片对象
 *
 * @author Liujinxin
 */
public class ImagesAttr {

    private String src;

    private int w = 110;

    private int h = 130;

    public ImagesAttr() {
    }

    public ImagesAttr(String src, int w, int h) {
        this.src = src;
        this.w = w;
        this.h = h;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
