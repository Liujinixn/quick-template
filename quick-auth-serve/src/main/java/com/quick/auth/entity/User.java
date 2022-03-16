package com.quick.auth.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends Online implements Serializable {

    private static final long serialVersionUID = -8736616045315083846L;

    @ApiModelProperty(value = "id标识")
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 用户名
     */
    @ExcelProperty("账户")
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 密码
     */
    @ExcelProperty("密码")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 加密盐值
     */
    @ApiModelProperty(value = "加密盐值")
    private String salt;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String phone;

    /**
     * 性别：1男2女
     */
    @ApiModelProperty(value = "性别[1男、2女]")
    private Integer sex;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 用户状态：1有效; 0无效
     */
    @ApiModelProperty(value = "用户状态[1有效、0无效]")
    private Integer status;

    /**
     * 最后登录时间
     */
    @ExcelProperty("登录时间")
    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    /**
     * 登录ip
     */
    @ApiModelProperty(value = "登录ip")
    private String loginIpAddress;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String headPortrait;

    /**
     * 角色
     */
    private List<Role> roles;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;
    }
}
