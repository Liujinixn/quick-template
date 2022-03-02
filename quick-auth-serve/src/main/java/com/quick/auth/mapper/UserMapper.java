package com.quick.auth.mapper;

import com.quick.auth.entity.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    /**
     * 根据用户名查询用户详细信息（shiro使用）
     *
     * @param username 用户名
     * @param status   状态
     * @return user
     */
    User findUserAllInfoInfoByUsername(String username, Integer status);

    /**
     * 根据用户名查询用户的基本信息（shiro使用）
     *
     * @param username 用户名
     * @param status   状态
     * @return user
     */
    User findUserSimpleInfoByUsername(String username, Integer status);

    /**
     * 根据用户ID查询用户（获取基本信息不含角色身份）
     *
     * @param userId 用户id
     * @return user
     */
    User findUserByUserId(String userId, Integer status);

    /**
     * 根据userId获取用户信息（含角色名称，不含权限名称）
     *
     * @param userId 用户id
     * @return user
     */
    User findUserAndRoleByUserId(String userId, Integer status);

    /**
     * 更新最后登录时间（shiro登录后使用）
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(String userId);

    /**
     * 根据用户id更新用户信息
     *
     * @param user 用户操作对象
     * @return int 影响行
     */
    int updateByUserId(User user);

    /**
     * 根据参数批量修改用户状态
     *
     * @param params 参数结构：{userIds：[1,2,3], status: 状态值}
     * @return int 影响行
     */
    int updateStatusBatch(Map<String, Object> params);

    /**
     * 根据角色id查询用户列表
     *
     * @param roleId 角色ID
     * @return list
     */
    List<User> findByRoleId(String roleId);

    /**
     * 根据角色id查询用户list
     *
     * @param roleIds 角色ID集合
     * @return list
     */
    List<User> findByRoleIds(List<String> roleIds);

    /**
     * 获取所有用户基本信息
     *
     * @param user 模糊查询条件【可选匹配参数：userId , username，email，phone,status】
     * @return list 用户列表
     */
    List<User> findUsers(User user);

    /**
     * 添加用户基本信息
     *
     * @param user 用户操作对象
     * @return 影响行数
     */
    int insertUser(User user);

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @param userId   根据userId排除某个用户
     * @param status   状态
     * @return int 数量
     */
    int findUsersWhetherExistByUsernameOrUserId(String username, String userId, Integer status);
}

