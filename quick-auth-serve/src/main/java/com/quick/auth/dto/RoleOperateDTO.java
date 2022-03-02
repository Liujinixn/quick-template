package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleOperateDTO {

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true)
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", required = true)
    private String description;

}
