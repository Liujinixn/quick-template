package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    /**
     * 旧密码
     */
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

    /**
     * 确认新密码
     */
    @ApiModelProperty(value = "确认新密码", required = true)
    private String confirmNewPassword;
}
