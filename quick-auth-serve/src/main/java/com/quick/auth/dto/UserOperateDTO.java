package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOperateDTO {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    /**
     * 性别：1男2女
     */

    @ApiModelProperty(value = "性别（1:男  2：女）", example = "1", required = true)
    private Integer sex;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄", example = "18", required = true)
    private Integer age;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", example = "", required = false)
    private String headPortrait;

}
