package com.quick.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单结构响应信息
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuVo {

    /**
     * 菜单ID（主键）
     */
    @ApiModelProperty(value = "菜单ID")
    private String permissionId;

    /**
     * 菜单（权限）名称
     */
    @ApiModelProperty(value = "菜单（权限）名称")
    private String name;

    /**
     * 权限描述
     */
    @ApiModelProperty(value = "权限描述")
    private String description;

    /**
     * 权限访问路径
     */
    @ApiModelProperty(value = "访问路径")
    private String url;

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
     * 下级菜单
     */
    @ApiModelProperty(value = "下级菜单")
    private List<MenuVo> menuList;

}
