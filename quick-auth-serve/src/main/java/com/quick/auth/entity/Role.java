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
public class Role extends PublicTime implements Serializable {

    private static final long serialVersionUID = 8981844145479424700L;

    @ApiModelProperty(value = "id标识")
    private Integer id;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private String roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述")
    private String description;

    /**
     * 状态：1有效; 0无效
     */
    @ApiModelProperty(value = "状态[1有效、0无效]")
    private Integer status;

    /**
     * 权限
     */
    @ApiModelProperty(value = "权限")
    private List<Permission> permissions;

}
