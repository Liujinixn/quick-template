package com.quick.auth.controller;

import com.github.pagehelper.PageInfo;
import com.quick.auth.dto.RoleAddOperateDTO;
import com.quick.auth.dto.RoleUpdateOperateDTO;
import com.quick.auth.entity.Role;
import com.quick.auth.entity.User;
import com.quick.auth.service.RoleService;
import com.quick.auth.shiro.realm.UserRealm;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${request.prefix.auth_server}/role")
@Api(tags = "角色模块")
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    UserRealm userRealm;

    /**
     * 获取角色列表数据
     *
     * @param name  角色名称（模糊匹配）
     * @param page  页码
     * @param limit 页长
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "角色名（关键字）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "int", example = "1", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "页长", defaultValue = "10", dataType = "int", example = "10", paramType = "query")
    })
    public Result<PageInfo<Role>> roleList(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<Role> roles = roleService.findRoles(name, page, limit);
        return Result.ok(roles);
    }

    /**
     * 新增角色
     *
     * @param roleDTO 角色操作对象
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增角色信息")
    public Result addRole(@RequestBody @Valid RoleAddOperateDTO roleDTO) {
        if (roleService.findRolesWhetherExistByRoleNameOrRoleId(roleDTO.getName(), null) > 0) {
            return Result.build("角色名已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        if (roleService.insertRole(role) <= 0) {
            return Result.build("添加失败");
        }
        return Result.ok("添加成功");
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除角色信息")
    @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "string", paramType = "query")
    public Result deleteRole(String roleId) {
        if (roleService.findByRoleId(roleId).size() > 0) {
            return Result.build("删除失败,该角色下存在用户");
        }
        List<String> roleIdsList = Arrays.asList(roleId);
        if (roleService.updateStatusBatch(roleIdsList) <= 0) {
            return Result.build("删除角色失败");
        }
        return Result.ok("删除角色成功");
    }

    /**
     * 批量删除角色
     *
     * @param roleIdStr 角色ID（多参数使用，隔开）
     */
    @DeleteMapping("/batch/delete")
    @ApiOperation(value = "删除角色信息（批量）")
    @ApiImplicitParam(name = "roleIdStr", value = "角色ID（多个角色ID使用 , 隔开）", required = true, dataType = "string", paramType = "query")
    public Result batchDeleteRole(String roleIdStr) {
        String[] roleIds = roleIdStr.split(",");
        List<String> roleIdsList = Arrays.asList(roleIds);
        if (roleService.findByRoleIds(roleIdsList).size() > 0) {
            return Result.build("删除失败,选择的角色下存在用户");
        }
        if (roleService.updateStatusBatch(roleIdsList) <= 0) {
            return Result.build("删除角色失败");
        }
        return Result.ok("删除角色成功");
    }

    /**
     * 编辑角色
     *
     * @param roleDTO 角色操作对象
     */
    @PutMapping("/edit")
    @ApiOperation(value = "编辑角色信息")
    public Result editRole(@RequestBody @Valid RoleUpdateOperateDTO roleDTO) {
        if (roleService.findRolesWhetherExistByRoleNameOrRoleId(roleDTO.getName(), roleDTO.getRoleId()) > 0) {
            return Result.build("角色名已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        if (roleService.updateByRoleId(role) <= 0) {
            return Result.build("编辑角色失败");
        }
        return Result.ok("编辑角色成功");
    }

    /**
     * 分配权限
     *
     * @param roleId          角色ID
     * @param permissionIdStr 权限ID（多参数使用，隔开）
     */
    @GetMapping("/assign/permission")
    @ApiOperation(value = "分配角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "permissionIdStr", value = "权限ID（多个角色ID使用 , 隔开）", required = true, dataType = "string", paramType = "query")
    })
    public Result assignRole(String roleId, String permissionIdStr) {
        List<String> permissionIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(permissionIdStr)) {
            String[] permissionIds = permissionIdStr.split(",");
            permissionIdsList = Arrays.asList(permissionIds);
        }
        if (roleService.addAssignPermission(roleId, permissionIdsList) <= 0) {
            return Result.build("分配权限失败");
        }
        /*重新加载角色下所有用户权限*/
        List<User> userList = roleService.findByRoleId(roleId);
        if (userList.size() > 0) {
            List<String> userIds = new ArrayList<>();
            for (User user : userList) {
                userIds.add(user.getUserId());
            }
            userRealm.clearAuthorizationByUserId(userIds);
        }
        return Result.ok("分配权限成功");
    }

    /**
     * 附属-获取所有角色列表数据
     */
    @GetMapping("/list/all")
    @ApiOperation(value = "附属-获取所有角色列表")
    public Result<List<Role>> roleAllList() {
        return Result.ok(roleService.findRoleAllList());
    }

}
