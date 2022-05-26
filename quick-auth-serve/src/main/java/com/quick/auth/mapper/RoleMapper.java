package com.quick.auth.mapper;

import com.quick.auth.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper {

    /**
     * 查询角色列表
     *
     * @param name   角色名称（模糊查询）
     * @param status 状态
     * @return list
     */
    List<Role> findRoles(String name, Integer status);

    /**
     * 添加角色
     *
     * @param role 角色对象
     * @return 影响行数
     */
    int insertRole(Role role);

    /**
     * 根据参数批量更新状态
     *
     * @param params 参数结构：{roleIds：[1,2,3], status: 状态值}
     * @return int 影响行数
     */
    int updateStatusBatch(Map<String, Object> params);

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
     * @param status 状态
     * @return int 数量
     */
    int findRolesWhetherExistByRoleNameOrRoleId(String name, String roleId, Integer status);

    /**
     * 查询 {roleIds} 中不能删除的角色信息
     *
     * @param roleIds      角色ID列表
     * @param notCanDelete 是否运行删除标识
     * @return 角色信息列表
     */
    List<Role> findRolesThatCannotDeletedByRoleId(List<String> roleIds, Integer notCanDelete);
}
