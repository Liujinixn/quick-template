package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 更改密码DTO
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    /**
     * 旧密码
     */
    @ApiModelProperty(value = "旧密码", required = true)
    @NotEmpty(message = "旧密码 不可以为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码", required = true)
    @NotEmpty(message = "新密码 不可以为空")
    private String newPassword;

    /**
     * 确认新密码
     */
    @ApiModelProperty(value = "确认新密码", required = true)
    @NotEmpty(message = "确认新密码 不可以为空")
    private String confirmNewPassword;
}
