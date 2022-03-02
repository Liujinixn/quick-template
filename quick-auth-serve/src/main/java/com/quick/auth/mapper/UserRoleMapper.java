package com.quick.auth.mapper;

import com.quick.auth.entity.UserRole;

import java.util.List;

public interface UserRoleMapper {

    /**
     * 根据用户ID删除用户和角色的关联关系
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int delete(String userId);

    /**
     * 批量添加用户角色关联关系
     *
     * @param userRoleList 户角色关联关系集合
     * @return 影响行数
     */
    int batchInstall(List<UserRole> userRoleList);
}
