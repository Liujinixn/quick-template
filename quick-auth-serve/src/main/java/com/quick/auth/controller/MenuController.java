package com.quick.auth.controller;

import com.quick.auth.constant.AuthServeCoreConst;
import com.quick.auth.service.PermissionService;
import com.quick.auth.vo.MenuVo;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${request.prefix.auth_server}/menu")
@Api(tags = "菜单模块")
public class MenuController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取当前登录用户的菜单
     */
    @GetMapping("/list/tree")
    @ApiOperation(value = "获取菜单列表（基本Tree结构）")
    public Result<List<MenuVo>> findMenus() {
        return Result.ok(permissionService.findAllMenuInfoLevel(AuthServeCoreConst.TOP_MENU_ID));
    }

}
