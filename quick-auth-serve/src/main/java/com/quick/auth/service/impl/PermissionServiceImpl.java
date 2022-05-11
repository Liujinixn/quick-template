package com.quick.auth.service.impl;

import com.quick.auth.entity.Permission;
import com.quick.auth.mapper.PermissionMapper;
import com.quick.auth.service.PermissionService;
import com.quick.auth.utils.ShiroUtil;
import com.quick.auth.vo.MenuVo;
import com.quick.common.utils.constant.CoreConst;
import com.quick.common.utils.constant.RedisDataCacheKey;
import com.quick.common.utils.redis.RedisClient;
import com.quick.common.utils.uuid.UUIDUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAllPermissionList() {
        List<Permission> permissionList = (List<Permission>) RedisClient.get(RedisDataCacheKey.PERMISSION_ALL);
        if (CollectionUtils.isNotEmpty(permissionList)) {
            return permissionList;
        }
        permissionList = permissionMapper.findAllPermissionList(CoreConst.STATUS_VALID);
        RedisClient.set(RedisDataCacheKey.PERMISSION_ALL, permissionList, RedisDataCacheKey.EXPIRED_TEN_MINUTE);
        return permissionList;
    }

    @Override
    public List<Permission> findAllPermissionListLevel(Integer parentId) {
        return permissionMapper.findAllPermissionListLevel(parentId, CoreConst.STATUS_VALID);
    }

    @Override
    public List<MenuVo> findAllMenuInfoLevel(Integer parentId) {
        // 获取当前登录用户的ID
        String loginUserId = ShiroUtil.getLoginUserId();
        return permissionMapper.findAllMenuInfoLevel(loginUserId, parentId, CoreConst.STATUS_VALID);
    }

    @Override
    public Permission findPermissionSimpleInfoByUrl(String url) {
        if(StringUtils.isBlank(url)){
            return null;
        }
        return permissionMapper.findPermissionSimpleInfoByUrl(url,CoreConst.STATUS_VALID);
    }

    @Override
    public int installPermission(Permission permission) {
        permission.setPermissionId(UUIDUtil.getUniqueIdByUUId());
        permission.setStatus(CoreConst.STATUS_VALID);
        return permissionMapper.insertPermission(permission);
    }

    @Override
    public int updatePermission(Permission permission) {
        return permissionMapper.updatePermission(permission);
    }

    @Override
    public int deletePermission(String permissionId) {
        return permissionMapper.updatePermissionStatus(permissionId, CoreConst.STATUS_INVALID);
    }

    @Override
    public int selectSubPermsByPermissionId(String parentId) {
        return permissionMapper.selectSubPermsByPermissionId(parentId, CoreConst.STATUS_VALID);
    }

    @Override
    public int findPermissionsWhetherExistByPermissionNameOrPermissionId(String name, String permissionId) {
        return permissionMapper.findPermissionsWhetherExistByPermissionNameOrPermissionId(name, permissionId, CoreConst.STATUS_VALID);
    }

}
