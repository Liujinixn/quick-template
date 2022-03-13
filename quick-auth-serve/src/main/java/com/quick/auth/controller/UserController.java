package com.quick.auth.controller;

import com.github.pagehelper.PageInfo;
import com.quick.auth.dto.ChangePasswordDTO;
import com.quick.auth.dto.UserOperateDTO;
import com.quick.auth.entity.User;
import com.quick.auth.service.UserService;
import com.quick.auth.shiro.ShiroCoreParameters;
import com.quick.auth.shiro.realm.UserRealm;
import com.quick.auth.utils.ShiroUtil;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRealm userRealm;

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    /**
     * 获取用户列表数据
     *
     * @param username 用户名称（模糊匹配）
     * @param phone    手机号（模糊匹配）
     * @param email    邮箱（模糊匹配）
     * @param page     页码
     * @param limit    页长
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名（关键字）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "手机号（关键字）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱（关键字）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "int", example = "1", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "页长", defaultValue = "10", dataType = "int", example = "10", paramType = "query")
    })
    public Result<PageInfo<User>> userList(@RequestParam(value = "username", required = false) String username,
                                           @RequestParam(value = "phone", required = false) String phone,
                                           @RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<User> users = userService.findUsers(new User(username, phone, email), page, limit);
        return Result.ok(users);
    }

    /**
     * 新增用户
     *
     * @param userDTO 用户操作对象
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增用户信息")
    @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "query")
    public Result addUser(UserOperateDTO userDTO, String password) {
        // 判断用户是否存在
        if (userService.findUsersWhetherExistByUsernameOrUserId(userDTO.getUsername(), null) > 0) {
            return Result.build("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        // 获取加密次数
        int hashIterations = shiroCoreParameters.getHashIterations();
        password = new Md5Hash(password, null, hashIterations).toString();
        user.setPassword(password);
        int res = userService.insertUser(user);
        if (res <= 0) {
            return Result.build("添加失败");
        }
        return Result.ok("添加成功");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "string", paramType = "query")
    public Result deleteUser(String userId) {
        List<String> userIdsList = Arrays.asList(userId);
        int res = userService.updateStatusBatch(userIdsList);
        if (res <= 0) {
            return Result.build("删除用户失败");
        } else {
            return Result.ok("删除用户成功");
        }
    }

    /**
     * 批量删除用户
     *
     * @param userIdStr 用户ID （多参数使用，隔开）
     */
    @DeleteMapping("/batch/delete")
    @ApiOperation(value = "删除用户信息（批量）")
    @ApiImplicitParam(name = "userIdStr", value = "用户ID（多个用户ID使用 , 隔开）", required = true, dataType = "string", paramType = "query")
    public Result batchDeleteUser(String userIdStr) {
        String[] userIds = userIdStr.split(",");
        List<String> userIdsList = Arrays.asList(userIds);
        int res = userService.updateStatusBatch(userIdsList);
        if (res <= 0) {
            return Result.build("删除用户失败");
        } else {
            return Result.ok("删除用户成功");
        }
    }

    /**
     * 编辑用户
     *
     * @param userId  用户ID
     * @param userDTO 用户操作对象
     */
    @PutMapping("/edit")
    @ApiOperation(value = "编辑用户信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "string", paramType = "query")
    public Result editUser(String userId, UserOperateDTO userDTO) {
        // 判断用户是否存在
        if (userService.findUsersWhetherExistByUsernameOrUserId(userDTO.getUsername(), userId) > 0) {
            return Result.build("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setUserId(userId);
        int res = userService.updateByUserId(user);
        if (res <= 0) {
            return Result.build("编辑用户失败");
        } else {
            return Result.ok("编辑用户成功");
        }
    }

    /**
     * 分配角色
     *
     * @param userId    用户ID
     * @param roleIdStr 角色ID（多参数使用，隔开）
     */
    @PostMapping("/assign/role")
    @ApiOperation(value = "分配用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "roleIdStr", value = "角色ID（多个角色ID使用 , 隔开）", required = true, dataType = "string", paramType = "query")
    })
    public Result assignRole(String userId, String roleIdStr) {
        String[] roleIds = roleIdStr.split(",");
        List<String> roleIdsList = Arrays.asList(roleIds);
        if (userService.addAssignRole(userId, roleIdsList) <= 0) {
            return Result.build("分配角色失败");
        }
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userRealm.clearAuthorizationByUserId(userIds);
        return Result.ok("分配角色成功");
    }

    /**
     * 强制用户下线
     *
     * @param userId 用户id
     */
    @GetMapping("/online/kickout")
    @ApiOperation(value = "强制用户下线")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "string", paramType = "query")
    public Result kickout(String userId) {
        try {
            userService.kickout(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok("操作失败");
        }
        return Result.ok("操作成功");
    }

    /**
     * 批量强制用户下线
     *
     * @param userIdStr 用户id（多个用户ID使用 , 隔开）
     */
    @GetMapping("/online/batch/kickout")
    @ApiOperation(value = "强制用户下线（批量）")
    @ApiImplicitParam(name = "userIdStr", value = "用户ID（多个用户ID使用 , 隔开）", required = true, dataType = "string", paramType = "query")
    public Result batchKickout(String userIdStr) {
        try {
            userService.kickout(userIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok("操作失败");
        }
        return Result.ok("操作成功");
    }

    /**
     * 附属-获取当前登录用户的信息（含角色信息，不包含角色的权限信息）
     */
    @GetMapping("/loginUserInfo")
    @ApiOperation(value = "附属-获取登录用户信息")
    public Result<User> getLoginUserAllInfo() {
        return Result.ok(userService.getLoginUserAllInfo());
    }

    /**
     * 附属-修改当前登录的用户密码
     *
     * @param changePasswordDTO 更改密码操作对象
     */
    @PostMapping(value = "/changePassword")
    @ApiOperation(value = "附属-修改账户密码")
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            return Result.build("两次密码输入不一致");
        }

        //* loginUser当前登录的用户信息 *//
        User loginUser = userService.findUserByUserId(ShiroUtil.getLoginUserId());
        //* 验证旧密码是否不一样 *//
        int hashIterations = shiroCoreParameters.getHashIterations();
        String oldPasswordEncryption = new Md5Hash(changePasswordDTO.getOldPassword(), null, hashIterations).toString();
        if (!loginUser.getPassword().equals(oldPasswordEncryption)) {
            return Result.build("您输入的旧密码有误");
        }
        //* 开始修改用户密码 *//
        String newPasswordEncryption = new Md5Hash(changePasswordDTO.getNewPassword(), null, hashIterations).toString();
        User user = new User(loginUser.getUserId(), newPasswordEncryption);
        int res = userService.updateByUserId(user);
        if (res <= 0) {
            return Result.build("密码修改失败");
        }
        //* 清除登录身份验证信息缓存 *//
        List<String> userIds = new ArrayList<>();
        userIds.add(loginUser.getUserId());
        userRealm.removeCachedAuthenticationInfo(userIds);
        /*SecurityUtils.getSubject().logout();*/
        return Result.ok("修改密码成功");
    }

}
