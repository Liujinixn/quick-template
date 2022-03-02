package com.quick.auth.mapper;

import com.quick.auth.entity.RolePermission;

import java.util.List;

public interface RolePermissionMapper {

    /**
     * 根据角色ID删除角色和权限的关联关系
     *
     * @param roleId 用户ID
     * @return 影响行数
     */
    int delete(String roleId);

    /**
     * 批量添加角色权限关联关系
     *
     * @param rolePermissionList 角色权限关联关系集合
     * @return 影响行数
     */
    int batchInstall(List<RolePermission> rolePermissionList);
}
