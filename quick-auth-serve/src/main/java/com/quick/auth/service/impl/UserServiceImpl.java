package com.quick.auth.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.entity.User;
import com.quick.auth.entity.UserRole;
import com.quick.auth.mapper.UserMapper;
import com.quick.auth.mapper.UserRoleMapper;
import com.quick.auth.service.UserService;
import com.quick.auth.shiro.filter.KickoutSessionControlFilter;
import com.quick.auth.shiro.realm.UserRealm;
import com.quick.auth.utils.ShiroUtil;
import com.quick.common.utils.constant.CoreConst;
import com.quick.common.utils.redis.RedisClient;
import com.quick.common.utils.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ShiroCoreParameters shiroCoreParameters;

    @Override
    public User findUserSimpleInfoByUsername(String username) {
        return userMapper.findUserSimpleInfoByUsername(username, CoreConst.STATUS_VALID);
    }

    @Override
    public User findUserAllInfoInfoByUsername(String username) {
        return userMapper.findUserAllInfoInfoByUsername(username, CoreConst.STATUS_VALID);
    }

    @Override
    public User findUserAllInfoInfoByUserId(String userId) {
        return userMapper.findUserAllInfoInfoByUserId(userId, CoreConst.STATUS_VALID);
    }

    @Override
    public void updateLastLoginTimeByUserId(String userId, String loginIpAddress) {
        userMapper.updateLastLoginTime(userId, loginIpAddress);
    }

    @Override
    public PageInfo<User> findUsers(User user, int page, int limit) {
        PageHelper.startPage(page, limit);
        user.setStatus(CoreConst.STATUS_VALID);
        List<User> users = userMapper.findUsers(user);

        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        // 设置用户在线情况
        for (User userInfo : users) {
            String username = userInfo.getUsername();
            LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(username);
            if (chcheOnlineUser == null || chcheOnlineUser.size() <= 0) {
                // 不在线
                userInfo.setOnlineStatusAndOnlineQuantity(CoreConst.NOT_ONLINE);
            } else {
                // 在线
                userInfo.setOnlineStatusAndOnlineQuantity(CoreConst.ONLINE, chcheOnlineUser.size());
            }
        }
        return new PageInfo<>(users);
    }

    @Override
    public User findUserByUserId(String userId) {
        return userMapper.findUserByUserId(userId, CoreConst.STATUS_VALID);
    }

    @Override
    public int insertUser(User user) {
        user.setUserId(UUIDUtil.getUniqueIdByUUId());
        user.setStatus(CoreConst.STATUS_VALID);
        return userMapper.insertUser(user);
    }

    @Override
    public int updateStatusBatch(List<String> userIds) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("userIds", userIds);
        params.put("status", CoreConst.STATUS_INVALID);
        return userMapper.updateStatusBatch(params);
    }

    @Override
    public int updateByUserId(User user) {
        return userMapper.updateByUserId(user);
    }

    @Override
    @Transactional
    public int addAssignRole(String userId, List<String> roleIds) {
        userRoleMapper.delete(userId);
        // 包装新的用户角色关系 入库
        List<UserRole> userRoleList = new ArrayList<>();
        for (String roleId : roleIds) {
            userRoleList.add(new UserRole(userId, roleId));
        }
        return userRoleMapper.batchInstall(userRoleList);
    }

    @Override
    public User getLoginUserAllInfo() {
        String loginUserId = ShiroUtil.getLoginUserId();
        if (null == loginUserId) {
            return null;
        }
        User userInfo = userMapper.findUserAndRoleByUserId(loginUserId, CoreConst.STATUS_VALID);

        // 获取用户在线情况
        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(userInfo.getUsername());
        if (chcheOnlineUser == null || chcheOnlineUser.size() <= 0) {
            // 不在线
            userInfo.setOnlineStatusAndOnlineQuantity(CoreConst.NOT_ONLINE);
        } else {
            // 在线
            userInfo.setOnlineStatusAndOnlineQuantity(CoreConst.ONLINE, chcheOnlineUser.size());
        }
        return userInfo;
    }

    @Override
    public void kickout(String userIds) {
        String[] userIdArray = userIds.split(",");

        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        for (String userIdVal : userIdArray) {
            User user = userMapper.findUserByUserId(userIdVal, CoreConst.STATUS_VALID);
            // 获取在线用户的令牌，并删除认证信息
            LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(user.getUsername());
            for (Serializable serializable : chcheOnlineUser) {
                RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixUserAuth() + serializable.toString());
            }
            // 删除用户的在线人数信息
            RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixOnline() + user.getUsername());
            // 删除用户的授权信息缓存
            String classUrl = UserRealm.class.getName();
            RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixOther()
                    + classUrl + ".authorizationCache:" + user.getId());
        }
    }

    @Override
    public int findUsersWhetherExistByUsernameOrUserId(String username, String userId) {
        return userMapper.findUsersWhetherExistByUsernameOrUserId(username, userId, CoreConst.STATUS_VALID);
    }

}















