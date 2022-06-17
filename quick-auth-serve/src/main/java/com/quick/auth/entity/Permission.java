package com.quick.auth.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends PublicTime implements Serializable {

    private static final long serialVersionUID = 609166403451181567L;

    /**
     * 权限id
     */
    @ApiModelProperty(value = "权限id")
    private String permissionId;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "菜单（权限）名称")
    private String name;

    /**
     * 权限描述
     */
    @ApiModelProperty(value = "菜单（权限）描述")
    private String description;

    /**
     * 权限访问路径
     */
    @ApiModelProperty(value = "权限访问路径")
    private String url;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识")
    private String perms;

    /**
     * 父级权限id
     */
    @ApiModelProperty(value = "父级权限id")
    private Integer parentId;

    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    @ApiModelProperty(value = "类型（0:目录   1:菜单   2:按钮）")
    private Integer type;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 状态：1有效; 0无效
     */
    @ApiModelProperty(value = "状态（1:有效   0:无效）")
    private Integer status;

    /**
     * 下级权限
     */
    @ApiModelProperty(value = "下级权限")
    private List<Permission> permissionList;
}
