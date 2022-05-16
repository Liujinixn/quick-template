package com.quick.auth.enumerate;

import lombok.AllArgsConstructor;

/**
 * 权限类型枚举
 *
 * @author Liujinxin
 */
@AllArgsConstructor
public enum PermissionTypeEnum {

    DIRECTORY(0, "目录"),

    MENU(1, "菜单"),

    BUTTON(2, "按钮");

    private final Integer code;

    private final String description;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
