package com.quick.auth.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.constant.AuthServeCoreConst;
import com.quick.auth.entity.User;
import com.quick.auth.entity.UserRole;
import com.quick.auth.mapper.UserMapper;
import com.quick.auth.mapper.UserRoleMapper;
import com.quick.auth.service.UserService;
import com.quick.auth.shiro.filter.KickoutSessionControlFilter;
import com.quick.auth.shiro.realm.UserRealm;
import com.quick.auth.utils.ShiroUtil;
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
        return userMapper.findUserSimpleInfoByUsername(username, AuthServeCoreConst.STATUS_VALID);
    }

    @Override
    public User findUserAllInfoInfoByUsername(String username) {
        return userMapper.findUserAllInfoInfoByUsername(username, AuthServeCoreConst.STATUS_VALID);
    }

    @Override
    public User findUserAllInfoInfoByUserId(String userId) {
        return userMapper.findUserAllInfoInfoByUserId(userId, AuthServeCoreConst.STATUS_VALID);
    }

    @Override
    public void updateLastLoginTimeByUserId(String userId, String loginIpAddress) {
        userMapper.updateLastLoginTime(userId, loginIpAddress);
    }

    @Override
    public PageInfo<User> findUsers(User user, int page, int limit) {
        PageHelper.startPage(page, limit);
        user.setStatus(AuthServeCoreConst.STATUS_VALID);
        List<User> users = userMapper.findUsers(user);

        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        // ????????????????????????
        for (User userInfo : users) {
            String username = userInfo.getUsername();
            LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(username);
            if (chcheOnlineUser == null || chcheOnlineUser.size() <= 0) {
                // ?????????
                userInfo.setOnlineStatusAndOnlineQuantity(AuthServeCoreConst.NOT_ONLINE);
            } else {
                // ??????
                userInfo.setOnlineStatusAndOnlineQuantity(AuthServeCoreConst.ONLINE, chcheOnlineUser.size());
            }
        }
        return new PageInfo<>(users);
    }

    @Override
    public User findUserByUserId(String userId) {
        return userMapper.findUserByUserId(userId, AuthServeCoreConst.STATUS_VALID);
    }

    @Override
    public int insertUser(User user) {
        user.setUserId(UUIDUtil.getUniqueIdByUUId());
        user.setStatus(AuthServeCoreConst.STATUS_VALID);
        return userMapper.insertUser(user);
    }

    @Override
    public int updateStatusBatch(List<String> userIds) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("userIds", userIds);
        params.put("status", AuthServeCoreConst.STATUS_INVALID);
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
        // ?????????????????????????????? ??????
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
        User userInfo = userMapper.findUserAndRoleByUserId(loginUserId, AuthServeCoreConst.STATUS_VALID);

        // ????????????????????????
        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(userInfo.getUsername());
        if (chcheOnlineUser == null || chcheOnlineUser.size() <= 0) {
            // ?????????
            userInfo.setOnlineStatusAndOnlineQuantity(AuthServeCoreConst.NOT_ONLINE);
        } else {
            // ??????
            userInfo.setOnlineStatusAndOnlineQuantity(AuthServeCoreConst.ONLINE, chcheOnlineUser.size());
        }
        return userInfo;
    }

    @Override
    public void kickout(String userIds) {
        String[] userIdArray = userIds.split(",");

        KickoutSessionControlFilter kickoutSessionControlFilter =
                SpringUtil.getBean("kickoutSessionControlFilter", KickoutSessionControlFilter.class);
        for (String userIdVal : userIdArray) {
            User user = userMapper.findUserByUserId(userIdVal, AuthServeCoreConst.STATUS_VALID);
            // ???????????????????????????????????????????????????
            LinkedList<Serializable> chcheOnlineUser = kickoutSessionControlFilter.getChcheOnlineUser(user.getUsername());
            for (Serializable serializable : chcheOnlineUser) {
                RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixUserAuth() + serializable.toString());
            }
            // ?????????????????????????????????
            RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixOnline() + user.getUsername());
            // ?????????????????????????????????
            String classUrl = UserRealm.class.getName();
            RedisClient.del(shiroCoreParameters.getShiroRedis().getPrefixOther()
                    + classUrl + ".authorizationCache:" + user.getUserId());
        }
    }

    @Override
    public int findUsersWhetherExistByUsernameOrUserId(String username, String userId) {
        return userMapper.findUsersWhetherExistByUsernameOrUserId(username, userId, AuthServeCoreConst.STATUS_VALID);
    }

}















