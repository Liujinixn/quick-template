package com.quick.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 系统登录入参DTO
 *
 * @author Liujinxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLoginDTO {

    /**
     * 登录账号
     */
    @NotEmpty(message = "账号不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

}
