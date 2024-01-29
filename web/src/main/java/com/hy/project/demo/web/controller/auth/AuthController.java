package com.hy.project.demo.web.controller.auth;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.Role;
import com.hy.project.demo.auth.facade.model.request.CreateNewPermissionRequest;
import com.hy.project.demo.auth.facade.model.request.CreateNewRoleRequest;
import com.hy.project.demo.auth.facade.model.request.PageQueryRequest;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateRolePermissionRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.auth.facade.service.PermissionService;
import com.hy.project.demo.auth.facade.service.RoleService;
import com.hy.project.demo.common.model.AjaxResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.web.model.PermissionCreateRequest;
import com.hy.project.demo.web.model.RoleCreateRequest;
import com.hy.project.demo.web.model.RolePermissionUpdateRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @DubboReference
    PermissionService permissionService;

    @DubboReference
    RoleService roleService;

    @PostMapping("/permission/create.json")
    public AjaxResult createPermission(@RequestBody PermissionCreateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");

        CreateNewPermissionRequest createNewPermissionRequest = new CreateNewPermissionRequest();
        createNewPermissionRequest.setName(request.getPermissionName());
        createNewPermissionRequest.setCode(request.getPermissionKey());

        RpcResult<String> result = permissionService.createNewPermission(RpcRequest.of(createNewPermissionRequest));
        return AjaxResult.success(result.getData());
    }

    @GetMapping("/permission/list.json")
    public AjaxResult listPermissions(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageQueryRequest pageQueryRequest = new PageQueryRequest();
        pageQueryRequest.setPageIndex(request.getPageIndex());
        pageQueryRequest.setPageSize(request.getPageSize());
        RpcResult<PageResult<List<Permission>>> result = permissionService.pageList(RpcRequest.of(pageQueryRequest));
        return AjaxResult.success(result.getData());
    }

    @PostMapping("/permission/deleteById.json")
    public AjaxResult deletePermissionById(@RequestBody Permission request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        permissionService.deletePermission(RpcRequest.of(request.getPermissionId()));
        return AjaxResult.success(null);
    }

    @PostMapping("/role/create.json")
    public AjaxResult createRole(@RequestBody RoleCreateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        CreateNewRoleRequest createNewRoleRequest = new CreateNewRoleRequest();
        createNewRoleRequest.setName(request.getRoleName());
        createNewRoleRequest.setCode(request.getRoleKey());
        createNewRoleRequest.setPermissions(request.getPermissionIds());
        RpcResult<String> result = roleService.createNewRole(RpcRequest.of(createNewRoleRequest));
        return AjaxResult.success(result.getData());
    }

    @GetMapping("/role/list.json")
    public AjaxResult listRoles(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageIndex(request.getPageIndex());
        pageRequest.setPageSize(request.getPageSize());
        RpcResult<PageResult<List<Role>>> result = roleService.pageList(RpcRequest.of(pageRequest));
        return AjaxResult.success(result.getData());
    }

    @PostMapping("/role/deleteById.json")
    public AjaxResult deleteRoleById(@RequestBody Role request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        roleService.deleteRole(RpcRequest.of(request.getRoleId()));
        return AjaxResult.success(null);
    }

    @PostMapping("/role/updateRolePermissions.json")
    public AjaxResult updateRolePermissions(@RequestBody RolePermissionUpdateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        UpdateRolePermissionRequest updateRolePermissionRequest = new UpdateRolePermissionRequest();
        updateRolePermissionRequest.setRoleId(request.getRoleId());
        updateRolePermissionRequest.setPermissions(request.getPermissionIds());
        roleService.updateRolePermissions(RpcRequest.of(updateRolePermissionRequest));
        return AjaxResult.success(null);
    }
}
