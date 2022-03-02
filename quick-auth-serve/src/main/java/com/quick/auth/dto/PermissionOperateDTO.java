package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增权限
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionOperateDTO {

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "菜单/权限名称", required=true)
    private String name;

    /**
     * 权限描述
     */
    @ApiModelProperty(value = "菜单/权限描述", required=true)
    private String description;

    /**
     * 权限访问路径
     */
    @ApiModelProperty(value = "权限访问路径", required=true)
    private String url;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识", required=true)
    private String perms;

    /**
     * 父级权限id
     */
    @ApiModelProperty(value = "父级权限id（0：顶级菜单）", required=true, example = "0")
    private Integer parentId;

    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    @ApiModelProperty(value = "类型（0:目录   1:菜单   2:按钮）", required=true, example = "0")
    private Integer type;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", required=true, example = "0")
    private Integer orderNum;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标", required=true)
    private String icon;

}
