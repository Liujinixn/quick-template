package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 新增权限DTO
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionAddOperateDTO {

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "菜单/权限名称", required = true)
    @NotEmpty(message = "菜单/权限名称 不可以为空")
    @Length(max = 50, message = "菜单/权限名称 最大长度为{max}")
    private String name;

    /**
     * 权限描述
     */
    @ApiModelProperty(value = "菜单/权限描述", required = true)
    @NotEmpty(message = "菜单/权限描述 不可以为空")
    @Length(max = 50, message = "菜单/权限描述 最大长度为{max}")
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
    @ApiModelProperty(value = "父级权限ID（0：顶级菜单）", required = true, example = "0")
    @NotNull(message = "父级权限ID 不可以为空")
    private Integer parentId;

    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    @ApiModelProperty(value = "类型（0:目录   1:菜单   2:按钮）", required = true, example = "0")
    @NotNull(message = "类型 不可以为空")
    @Range(min = 0, max = 2, message = "类型 可选值【0:目录  1:菜单  2:按钮】")
    private Integer type;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", required = true, example = "0")
    @NotNull(message = "排序 不可以为空")
    @Range(min = 0, max = 10000, message = "类型 可选值【{min}~{max}】")
    private Integer orderNum;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 50, message = "图标 最大长度为{max}")
    private String icon;

}
