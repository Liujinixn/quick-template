package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 角色编辑操作DTO
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateOperateDTO {

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色ID", required = true)
    @NotEmpty(message = "角色ID 不能为空")
    @Length(max = 20, message = "角色ID 最大长度为{max}")
    private String roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true)
    @NotEmpty(message = "角色名称 不能为空")
    @Length(max = 50, message = "角色名称 最大长度为{max}")
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", required = true)
    @NotEmpty(message = "角色描述 不能为空")
    @Length(max = 200, message = "角色描述 最大长度为{max}")
    private String description;

}
