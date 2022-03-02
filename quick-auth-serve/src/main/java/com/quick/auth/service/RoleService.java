package com.quick.auth.service;

import com.github.pagehelper.PageInfo;
import com.quick.auth.entity.Role;
import com.quick.auth.entity.User;

import java.util.List;

public interface RoleService {

    /**
     * 查询角色列表
     *
     * @param name  角色名称（模糊查询）
     * @param page  页码
     * @param limit 页长
     * @return PageInfo 数据列表含分页信息
     */
    PageInfo<Role> findRoles(String name, int page, int limit);

    /**
     * 添加角色
     *
     * @param role 角色对象
     * @return 影响行数
     */
    int insertRole(Role role);

    /**
     * 获取角色id下的所有用户
     *
     * @param roleId
     * @return list
     */
    List<User> findByRoleId(String roleId);

    /**
     * 批量更新状态
     *
     * @param roleIds 角色ID数据
     * @return int 影响行数
     */
    int updateStatusBatch(List<String> roleIds);

    /**
     * 根据角色id获取下面所有用户
     *
     * @param roleIds 角色ID集合
     * @return list
     */
    List<User> findByRoleIds(List<String> roleIds);

    /**
     * 根据角色id保存分配权限
     *
     * @param roleId            角色ID
     * @param permissionIdsList 权限ID集合
     * @return int 影响行数
     */
    int addAssignPermission(String roleId, List<String> permissionIdsList);

    /**
     * 根据角色id更新角色信息
     *
     * @param role 角色操作对象
     * @return int 影响行数
     */
    int updateByRoleId(Role role);

    /**
     * 查询角色名是否存在
     *
     * @param name   角色名
     * @param roleId 根据roleId排除某个角色
     * @return int 数量
     */
    int findRolesWhetherExistByRoleNameOrRoleId(String name, String roleId);

    /**
     * 查询所有角色列表（只包含角色ID角色名称）
     *
     * @return 角色列表
     */
    List<Role> findRoleAllList();
}
