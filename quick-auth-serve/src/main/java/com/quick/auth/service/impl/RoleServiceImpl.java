package com.quick.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quick.auth.constant.AuthServeCoreConst;
import com.quick.auth.entity.Role;
import com.quick.auth.entity.RolePermission;
import com.quick.auth.entity.User;
import com.quick.auth.mapper.RoleMapper;
import com.quick.auth.mapper.RolePermissionMapper;
import com.quick.auth.mapper.UserMapper;
import com.quick.auth.service.RoleService;
import com.quick.common.utils.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public PageInfo<Role> findRoles(String name, int page, int limit) {
        PageHelper.startPage(page, limit);
        List<Role> roles = roleMapper.findRoles(name, AuthServeCoreConst.STATUS_VALID);
        PageInfo<Role> roleListPageInfo = new PageInfo<>(roles);
        return roleListPageInfo;
    }

    @Override
    public int insertRole(Role role) {
        role.setRoleId(UUIDUtil.getUniqueIdByUUId());
        role.setStatus(AuthServeCoreConst.STATUS_VALID);
        role.setCanDelete(AuthServeCoreConst.CAN_DELETE);
        return roleMapper.insertRole(role);
    }

    @Override
    public List<User> findByRoleId(String roleId) {
        return userMapper.findByRoleId(roleId);
    }

    @Override
    @Transactional
    public int updateStatusBatch(List<String> roleIds) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("roleIds", roleIds);
        params.put("status", AuthServeCoreConst.STATUS_INVALID);
        return roleMapper.updateStatusBatch(params);
    }

    @Override
    public List<User> findByRoleIds(List<String> roleIds) {
        return userMapper.findByRoleIds(roleIds);
    }

    @Override
    @Transactional
    public int addAssignPermission(String roleId, List<String> permissionIds) {
        // ??????????????????
        rolePermissionMapper.delete(roleId);
        // ????????????????????????????????????
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for (String permissionId : permissionIds) {
            rolePermissionList.add(new RolePermission(roleId, permissionId));
        }
        return rolePermissionMapper.batchInstall(rolePermissionList);
    }

    @Override
    public int updateByRoleId(Role role) {
        return roleMapper.updateByRoleId(role);
    }

    @Override
    public int findRolesWhetherExistByRoleNameOrRoleId(String name, String roleId) {
        return roleMapper.findRolesWhetherExistByRoleNameOrRoleId(name, roleId, AuthServeCoreConst.STATUS_VALID);
    }

    @Override
    public List<Role> checkRolesThatCannotDeleted(List<String> roleIds) {
        return roleMapper.findRolesThatCannotDeletedByRoleId(roleIds, AuthServeCoreConst.NOT_CAN_DELETE);
    }

    @Override
    public List<Role> findRoleAllList() {
        return roleMapper.findRoles(null, AuthServeCoreConst.STATUS_VALID);
    }
}
