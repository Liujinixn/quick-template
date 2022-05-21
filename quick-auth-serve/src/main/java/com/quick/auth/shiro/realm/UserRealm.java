package com.quick.auth.shiro.realm;

import com.quick.auth.constant.AuthServeCoreConst;
import com.quick.auth.entity.Permission;
import com.quick.auth.entity.Role;
import com.quick.auth.entity.User;
import com.quick.auth.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 自定义Realm（验证账号密码登录）
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //拿到用户，如果认证返回的SimpleAuthenticationInfo对象 参数一 传入的是 user对象那这里取到的就是对象 如果是用户名 这里取到的就是用户名
        User user = (User) principals.getPrimaryPrincipal();
        /********查询出 当前用户的所有详细信息 包括用户的角色和权限 ***************/
        User userallInfo = userService.findUserAllInfoInfoByUsername(user.getUsername());

        //将用户的角色 和 所有的权限 都存储到这两个集合中即可，自己调用service层代码
        Set<String> stringRoleList = new HashSet<>();
        Set<String> stringPermissionList = new HashSet<>();

        for (Role role : userallInfo.getRoles()) {
            stringRoleList.add(role.getName());
            for (Permission permission : role.getPermissions()) {
                if (StringUtils.isNotBlank(permission.getPerms())) {
                    stringPermissionList.add(permission.getPerms());
                }
            }
        }
        /***********************************************************************/

        //将用户的角色 和 用户的权限 集合传进去，返回SimpleAuthorizationInfo对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(stringRoleList);
        simpleAuthorizationInfo.setStringPermissions(stringPermissionList);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        /*****************获取 当前登录的用户名 对应的用户信息*******************/
        User user = userService.findUserSimpleInfoByUsername(username);
        if (user == null) {
            return null;
        }
        if (AuthServeCoreConst.STATUS_INVALID.equals(user.getStatus())) {
            // 帐号锁定
            throw new LockedAccountException();
        }
        /***********************************************************************/
        Subject subject1 = SecurityUtils.getSubject();
        Session session = subject1.getSession();
        session.setAttribute("loginUser", user);

        // 将用户的密码和用户对象写进去返回，切记整合redis 必须要传对象
        return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
    }

    /**
     * 清除认证信息
     *
     * @param userIds 用户的ID集合
     */
    public void removeCachedAuthenticationInfo(List<String> userIds) {
        if (null == userIds || userIds.size() == 0) {
            return;
        }
        List<SimplePrincipalCollection> list = getSpcListByUserIds(userIds);
        RealmSecurityManager securityManager =
                (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm realm = (UserRealm) securityManager.getRealms().iterator().next();
        for (SimplePrincipalCollection simplePrincipalCollection : list) {
            realm.clearCachedAuthenticationInfo(simplePrincipalCollection);
        }
    }

    /**
     * 根据userId 清除当前session存在的用户的权限缓存
     *
     * @param userIds 已经修改了权限的userId
     */
    public void clearAuthorizationByUserId(List<String> userIds) {
        if (null == userIds || userIds.size() == 0) {
            return;
        }
        List<SimplePrincipalCollection> list = getSpcListByUserIds(userIds);
        RealmSecurityManager securityManager =
                (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm realm = (UserRealm) securityManager.getRealms().iterator().next();
        for (SimplePrincipalCollection simplePrincipalCollection : list) {
            // 清楚授权认证信息
            realm.clearCachedAuthorizationInfo(simplePrincipalCollection);
        }
    }

    /**
     * 根据用户id获取所有spc
     *
     * <p>
     * 其中 redisSessionDAO.getActiveSessions() 方法存在一定的使用问题：
     * SpringBoot高版本版本会导致NoSuchMethodError:redis/clients/jedis/ScanResult.getStringCursor()错误，
     * 但是目前可以确定springboot版本在 2.3.2.RELEASE -2.4.2之间的版本会出错，2.1.8.RELEASE 版本不会出现这个错误，
     * （具体错误原因和解决方案：https://blog.csdn.net/weixin_40516924/article/details/113409458）。
     * <p/>
     *
     * @param userIds 已经修改了权限的userId
     */
    private List<SimplePrincipalCollection> getSpcListByUserIds(List<String> userIds) {
        //获取所有session
        Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        //定义返回
        List<SimplePrincipalCollection> list = new ArrayList<SimplePrincipalCollection>();
        for (Session session : sessions) {
            //获取session登录信息。
            Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (null != obj && obj instanceof SimplePrincipalCollection) {
                //强转
                SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
                //判断用户，匹配用户ID。
                obj = spc.getPrimaryPrincipal();
                if (null != obj && obj instanceof User) {
                    User user = (User) obj;
                    //比较用户ID，符合即加入集合
                    if (null != user && userIds.contains(user.getUserId())) {
                        list.add(spc);
                    }
                }
            }
        }
        return list;
    }

}
