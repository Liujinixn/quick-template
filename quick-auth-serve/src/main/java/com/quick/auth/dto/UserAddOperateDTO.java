package com.quick.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 用户新增操作DTO
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddOperateDTO {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty(message = "用户名 不可以为空")
    @Length(max = 50, message = "用户名 最大长度为{max}")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码 不可以为空")
    @Length(max = 20, message = "密码 最大长度为{max}")
    private String password;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", required = true)
    @NotEmpty(message = "邮箱 不可以为空")
    @Length(max = 50, message = "邮箱 最大长度为{max}")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "手机号", required = true)
    @NotEmpty(message = "手机号 不可以为空")
    @Length(max = 11, message = "手机号 最大长度为{max}")
    private String phone;

    /**
     * 性别：1男 2女
     */
    @ApiModelProperty(value = "性别（1:男  2：女）", example = "1", required = true)
    @NotNull(message = "性别 不可以为空")
    @Range(min = 1, max = 2, message = "性别 可选值【1:男  2：女】")
    private Integer sex;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄", example = "18", required = true)
    @NotNull(message = "年龄 不可以为空")
    @Range(min = 1, max = 100, message = "年龄 可选值【{min}~{max}】")
    private Integer age;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", example = "", required = false)
    @Length(max = 255, message = "头像URL地址 最大长度为{max}")
    private String headPortrait;

}
