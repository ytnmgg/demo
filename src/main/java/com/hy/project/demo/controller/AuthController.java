package com.hy.project.demo.controller;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.controller.request.PageRequest;
import com.hy.project.demo.controller.request.PermissionCreateRequest;
import com.hy.project.demo.controller.request.RoleCreateRequest;
import com.hy.project.demo.controller.request.RolePermissionUpdateRequest;
import com.hy.project.demo.model.AjaxResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.user.Permission;
import com.hy.project.demo.model.user.Role;
import com.hy.project.demo.service.auth.PermissionService;
import com.hy.project.demo.service.auth.RoleService;
import com.hy.project.demo.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;

    @PostMapping("/permission/create.json")
    public AjaxResult createPermission(@RequestBody PermissionCreateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        String result = permissionService.createNewPermission(request.getPermissionName(), request.getPermissionKey());
        return AjaxResult.success(result);
    }

    @GetMapping("/permission/list.json")
    public AjaxResult listPermissions(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageResult<List<Permission>> result = permissionService.pageList(request.getPageIndex(),
            request.getPageSize());
        return AjaxResult.success(result);
    }

    @PostMapping("/permission/deleteById.json")
    public AjaxResult deletePermissionById(@RequestBody Permission request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        permissionService.deletePermission(request.getPermissionId());
        return AjaxResult.success(null);
    }

    @PostMapping("/role/create.json")
    public AjaxResult createRole(@RequestBody RoleCreateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        String result = roleService.createNewRole(request.getRoleName(), request.getRoleKey(),
            request.getPermissionIds());
        return AjaxResult.success(result);
    }

    @GetMapping("/role/list.json")
    public AjaxResult listRoles(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageResult<List<Role>> result = roleService.pageList(request.getPageIndex(),
            request.getPageSize());
        return AjaxResult.success(result);
    }

    @PostMapping("/role/deleteById.json")
    public AjaxResult deleteRoleById(@RequestBody Role request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        roleService.deleteRole(request.getRoleId());
        return AjaxResult.success(null);
    }

    @PostMapping("/role/updateRolePermissions.json")
    public AjaxResult updateRolePermissions(@RequestBody RolePermissionUpdateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        roleService.updateRolePermissions(request.getRoleId(), request.getPermissionIds());
        return AjaxResult.success(null);
    }
}
