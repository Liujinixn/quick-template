package com.quick.auth.constant;

import com.quick.common.utils.constant.CoreConst;

/**
 * 核心常量
 *
 * @author Liujinxin
 */
public class AuthServeCoreConst extends CoreConst {

    /**
     * 状态有效（正常状态）
     */
    public static final Integer STATUS_VALID = 1;

    /**
     * 状态无效（删除状态）
     */
    public static final Integer STATUS_INVALID = 0;

    /**
     * 顶层菜单ID
     */
    public static Integer TOP_MENU_ID = 0;

    /**
     * 顶层菜单名称
     */
    public static String TOP_MENU_NAME = "顶层菜单";

    /**
     * 用户在线（在线）
     */
    public static int ONLINE = 1;

    /**
     * 用户不在线（不在线）
     */
    public static int NOT_ONLINE = 0;
}
