package com.quick.auth.service;

import com.github.pagehelper.PageInfo;
import com.quick.auth.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 根据用户名查询用户的基本信息
     *
     * @param username 用户名
     * @return User对象
     */
    User findUserSimpleInfoByUsername(String username);

    /**
     * 根据用户名查询用户详细信息
     *
     * @param username 用户名
     * @return User对象
     */
    User findUserAllInfoInfoByUsername(String username);

    /**
     * 更新最后登录时间
     *
     * @param userId         用户ID
     * @param loginIpAddress 登录地址
     */
    void updateLastLoginTimeByUserId(String userId, String loginIpAddress);

    /**
     * 获取所有用户基本信息
     *
     * @param user  User对象查询条件【username,phone,email】三个参数均支持模糊匹配
     * @param page  页码
     * @param limit 页长
     * @return PageInfo 数据列表含分页信息
     */
    PageInfo<User> findUsers(User user, int page, int limit);

    /**
     * 根据用户ID获取用户信息（获取基本信息不含角色身份）
     *
     * @param userId 用户id
     * @return User对象
     */
    User findUserByUserId(String userId);

    /**
     * 添加用户基本信息
     *
     * @param user 用户操作对象
     * @return 影响行数
     */
    int insertUser(User user);

    /**
     * 根据用户id集合批量更新用户状态
     *
     * @param userIds 用户id（多个用户ID使用 , 隔开）
     * @return int 影响行数
     */
    int updateStatusBatch(List<String> userIds);

    /**
     * 根据用户id更新用户信息
     *
     * @param user 用户操作对象
     * @return int 影响行数
     */
    int updateByUserId(User user);

    /**
     * 根据用户id分配角色集合
     *
     * @param userId      用户ID
     * @param roleIdsList 角色ID集合
     * @return 影响行数
     */
    int addAssignRole(String userId, List<String> roleIdsList);

    /**
     * 获取登录用户的详细信息
     *
     * @return User对象
     */
    User getLoginUserAllInfo();

    /**
     * 强制用户下线
     *
     * @param userIds 用户id（多个用户ID使用 , 隔开）
     */
    void kickout(String userIds);

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @param userId   根据userId排除某个用户
     * @return int 数量
     */
    int findUsersWhetherExistByUsernameOrUserId(String username, String userId);

}
