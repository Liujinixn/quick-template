package com.quick.file.enumerate;

import lombok.AllArgsConstructor;

/**
 * 权限类型枚举
 *
 * @author Liujinxin
 */
@AllArgsConstructor
public enum FileSuffixTypeEnum {

    JPG("jpg", ".jpg"),
    PNG("png", ".png"),
    JPEG("jpeg", ".jpeg"),
    TXT("txt", ".txt"),
    PDF("pdf", ".pdf"),
    DOCX("docx", ".docx"),
    XLSX("xlsx", ".xlsx");

    /**
     * 文件类型标识Code
     */
    private final String code;

    /**
     * 文件后缀名
     */
    private final String description;

    /**
     * 根据后缀名匹配合适的 FileSuffixTypeEnum 对象
     *
     * @param suffix 后缀名
     * @return FileSuffixTypeEnum对象
     */
    public static FileSuffixTypeEnum getTypeEnumBySuffix(String suffix) {
        FileSuffixTypeEnum[] values = FileSuffixTypeEnum.values();
        for (FileSuffixTypeEnum e : values) {
            if (e.description.equalsIgnoreCase(suffix)) {
                return e;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
