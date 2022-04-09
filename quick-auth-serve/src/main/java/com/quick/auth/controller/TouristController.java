package com.quick.auth.controller;

import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.service.UserService;
import com.quick.auth.utils.ShiroUtil;
import com.quick.common.utils.ip.IpUtil;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${request.prefix.auth_server}/tourist")
@Api(tags = "认证模块")
public class TouristController {

    @Autowired
    UserService userService;

    @Autowired
    private ShiroCoreParameters shiroCoreParameters;

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录效验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "query")
    })
    public Result login(String username, String password, HttpServletRequest request) {
        // System.out.println(new Md5Hash(password, null, 2).toString());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        Map<String, Object> info = new HashMap<>(2);
        try {
            subject.login(usernamePasswordToken);
            info.put(shiroCoreParameters.getTokenKey(), shiroCoreParameters.getTokenValuePrefix() + subject.getSession().getId());
            // 计算过期时间
            int time = shiroCoreParameters.getTokenExpirationTime() / 60;
            info.put(shiroCoreParameters.getTokenKey() + "令牌有效期", time + "分钟");

        } catch (LockedAccountException e) {
            usernamePasswordToken.clear();
            return Result.build("用户已经被锁定不能登录，请联系管理员！");
        } catch (IncorrectCredentialsException ice) {
            return Result.build("用户名密码不匹配");
        } catch (AuthenticationException e) {
            return Result.build("用户名不存在，请先注册");
        } catch (Exception e) {
            return Result.build("登录异常，请稍后重试");
        }
        // 更新最后登录时间 和 登录IP
        String loginUserId = ShiroUtil.getLoginUserId();
        String loginIp = IpUtil.getIpAddr(request);
        userService.updateLastLoginTimeByUserId(loginUserId, loginIp);
        return Result.ok(info);
    }

    /**
     * 登出
     */
    @GetMapping(value = "/logout")
    @ApiOperation(value = "退出登录")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        if (null != subject) {
            String userId = ShiroUtil.getLoginUserId();
            userService.kickout(userId);
        }
        subject.logout();
        return Result.ok("退出成功");
    }

    /*
     * 未登录
     */
    /*@RequestMapping(value = "/noLogin", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "未登录/凭证失效")
    public Result noLogin() {
        return Result.build("登录凭证失效");
    }*/

    /*
     * 没有权限
     */
    /*@RequestMapping(value = "/noAuth", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "权限不足")
    public Result noAuth() {
        return Result.build("账户权限不足");
    }*/

    /*
     * 踢出登录接口（shiro重定向）
     */
    /*@RequestMapping(value = "/kickout", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "踢出登录")
    public Result kickout() {
        return Result.build("您已被踢出");
    }*/
}
