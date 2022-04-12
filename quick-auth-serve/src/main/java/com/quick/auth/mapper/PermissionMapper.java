package com.quick.auth.mapper;

import com.quick.auth.entity.Permission;
import com.quick.auth.vo.MenuVo;

import java.util.List;

public interface PermissionMapper {

    /**
     * 根据状态查询全部权限资源
     *
     * @param status 状态
     * @return list 无层级关系
     */
    List<Permission> findAllPermissionList(Integer status);

    /**
     * 查询全部权限
     *
     * @param parentId 父级ID
     * @param status   状态
     * @return list 有层级关系
     */
    List<Permission> findAllPermissionListLevel(Integer parentId, Integer status);

    /**
     * 查询当前用户可访问的菜单信息
     *
     * @param userId   用户user_id
     * @param parentId 父级ID
     * @param status   状态
     * @return list 有层级关系
     */
    List<MenuVo> findAllMenuInfoLevel(String userId, Integer parentId, Integer status);

    /**
     * 添加权限
     *
     * @param permission 权限对象
     * @return int 影响行数
     */
    int insertPermission(Permission permission);

    /**
     * 修改权限（根据权限ID修改）
     *
     * @param permission 权限对象
     * @return int 影响行数
     */
    int updatePermission(Permission permission);

    /**
     * 修改权限状态
     *
     * @param permissionId 权限ID
     * @param status       权限状态
     * @return int 影响行数
     */
    int updatePermissionStatus(String permissionId, Integer status);

    /**
     * 根据权限id查询有几个子资源
     *
     * @param parentId 权限id
     * @param status   状态
     * @return int 资源数量
     */
    int selectSubPermsByPermissionId(String parentId, Integer status);

    /**
     * 查询权限名是否存在
     *
     * @param name         权限名
     * @param permissionId 根据permissionId排除某个权限
     * @param status       状态
     * @return int 数量
     */
    int findPermissionsWhetherExistByPermissionNameOrPermissionId(String name, String permissionId, Integer status);

    /**
     * 工具url 查询权限信息
     *
     * @param url    url地址
     * @param status 状态
     * @return 权限信息
     */
    Permission findPermissionSimpleInfoByUrl(String url, Integer status);
}
