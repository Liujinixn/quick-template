package com.quick.auth.controller;

import com.quick.auth.dto.PermissionOperateDTO;
import com.quick.auth.entity.Permission;
import com.quick.auth.service.PermissionService;
import com.quick.common.vo.Result;
import com.quick.common.utils.constant.CoreConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${request.prefix.auth_server}/permission")
@Api(tags = "权限模块")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    /**
     * 获取权限列表数据
     */
    @GetMapping("/list")
    @ApiOperation("获取权限列表")
    public Result<List<Permission>> permissionList() {
        List<Permission> permissionList =
                permissionService.findAllPermissionListLevel(CoreConst.TOP_MENU_ID);
        return Result.ok(permissionList);
    }

    /**
     * 添加权限
     *
     * @param permissionDTO 添加权限对象
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增权限信息")
    public Result permissionAdd(PermissionOperateDTO permissionDTO) {
        if (permissionService.findPermissionsWhetherExistByPermissionNameOrPermissionId(permissionDTO.getName(), null) > 0) {
            return Result.build("权限名称已存在");
        }
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        int res = permissionService.installPermission(permission);
        if (res <= 0) {
            return Result.build("添加失败");
        }
        return Result.ok("添加成功");
    }

    /**
     * 编辑权限
     *
     * @param permissionId  权限ID（ 根据权限ID来修改数据）
     * @param permissionDTO 修改的权限数据
     */
    @PutMapping("/edit")
    @ApiOperation("编辑权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permissionId", value = "权限ID", required = true, dataType = "string", paramType = "query")
    })
    public Result permissionUpdate(String permissionId, PermissionOperateDTO permissionDTO) {
        if (permissionService.findPermissionsWhetherExistByPermissionNameOrPermissionId(permissionDTO.getName(), permissionId) > 0) {
            return Result.build("权限名称已存在");
        }
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        permission.setPermissionId(permissionId);
        int res = permissionService.updatePermission(permission);
        if (res <= 0) {
            return Result.build("修改失败");
        }
        return Result.ok("修改成功");
    }

    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permissionId", value = "权限ID", required = true, dataType = "string", paramType = "query")
    })
    public Result permissionDelete(String permissionId) {
        if (permissionService.selectSubPermsByPermissionId(permissionId) > 0) {
            return Result.build("该资源存在下级资源，无法删除！");
        }
        int res = permissionService.deletePermission(permissionId);
        if (res <= 0) {
            return Result.build("删除失败");
        }
        return Result.ok("删除成功");
    }

    /**
     * 附属-获取权限列表数据
     */
    @GetMapping("/list/all")
    @ApiOperation(value = "附属-获取所有角色列表")
    public Result<List<Permission>> permissionAllList() {
        List<Permission> permissionList =
                permissionService.findAllPermissionListLevel(CoreConst.TOP_MENU_ID);
        return Result.ok(permissionList);
    }

}
