package com.quick.auth.utils;

import com.quick.auth.entity.User;
import org.apache.shiro.SecurityUtils;

/**
 * shiro 相关操作
 *
 * @author Liujinxin
 */
public class ShiroUtil {

    /**
     * 获取当前登录用户的userId
     *
     * @return 用户编号，如果返回null，则说明当前没有登录
     */
    public static String getLoginUserId() {
        return null == getLoginUserInfo() ? null : getLoginUserInfo().getUserId();
    }

    /**
     * 获取当前登录用户的基本信息
     *
     * @return user, 如果返回null，则说明当前没有登录
     */
    public static User getLoginUserInfo() {
        if (null == SecurityUtils.getSubject().getPrincipal()) {
            return null;
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return user;
    }
}
