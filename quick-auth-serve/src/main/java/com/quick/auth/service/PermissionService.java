package com.quick.auth.service;

import com.quick.auth.entity.Permission;
import com.quick.auth.vo.MenuVo;

import java.util.List;

public interface PermissionService {

    /**
     * 查询全部权限
     *
     * @return list 无层级关系
     */
    List<Permission> findAllPermissionList();

    /**
     * 查询全部权限
     *
     * @param parentId 父级ID
     * @return list 有层级关系
     */
    List<Permission> findAllPermissionListLevel(Integer parentId);

    /**
     * 查询全部菜单信息
     *
     * @param parentId 父级ID
     * @return list 有层级关系
     */
    List<MenuVo> findAllMenuInfoLevel(Integer parentId);

    /**
     * 添加权限
     *
     * @param permission 权限对象
     * @return int 影响行数
     */
    int installPermission(Permission permission);

    /**
     * 修改权限（根据权限ID修改）
     *
     * @param permission 权限对象
     * @return int 影响行数
     */
    int updatePermission(Permission permission);

    /**
     * 删除权限（根据权限ID修改权限状态为删除状态）
     *
     * @param permissionId 权限ID
     * @return int 影响行数
     */
    int deletePermission(String permissionId);

    /**
     * 查询子权限条数
     *
     * @param parentId 父级权限ID
     * @return int 子权限条数
     */
    int selectSubPermsByPermissionId(String parentId);

    /**
     * 查询权限名是否存在
     *
     * @param name         权限名
     * @param permissionId 根据permissionId排除某个权限
     * @return int 数量
     */
    int findPermissionsWhetherExistByPermissionNameOrPermissionId(String name, String permissionId);
}
