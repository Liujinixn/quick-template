package com.quick.auth.utils;

import com.quick.auth.entity.User;
import org.apache.shiro.SecurityUtils;

/**
 * shiro 相关操作
 */
public class ShiroUtil {

    /**
     * 获取当前登录用户的userId
     *
     * @return 用户编号
     */
    public static String getLoginUserId() {
        return getLoginUserInfo().getUserId();
    }

    /**
     * 获取当前登录用户的基本信息
     *
     * @return user
     */
    public static User getLoginUserInfo() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return user;
    }
}
