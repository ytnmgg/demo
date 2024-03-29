package com.hy.project.demo.web.controller.auth;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.RegisterRequest;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserPasswordRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserRoleRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.auth.facade.service.LoginService;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.model.AjaxResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.ResultUtil;
import com.hy.project.demo.web.model.LoginWebRequest;
import com.hy.project.demo.web.model.UserRegisterRequest;
import com.hy.project.demo.web.model.UserRoleUpdateRequest;
import com.hy.project.demo.web.service.AuthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @DubboReference
    UserService userService;

    @DubboReference
    LoginService loginService;

    @DubboReference
    TokenService tokenService;

    @Autowired
    AuthService authService;

    @GetMapping("/list.json")
    public AjaxResult listUsers(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageIndex(request.getPageIndex());
        pageRequest.setPageSize(request.getPageSize());
        RpcResult<PageResult<List<SysUser>>> result = userService.pageListUsers(RpcRequest.of(pageRequest));
        return AjaxResult.success(result.getData());
    }

    @PostMapping("/register.json")
    public AjaxResult register(@RequestBody UserRegisterRequest request) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(request.getUsername());
        registerRequest.setPassword(request.getPassword());
        registerRequest.setRoleIds(request.getRoleIds());
        RpcResult<String> rpcResult = loginService.register(RpcRequest.of(registerRequest));
        return AjaxResult.success(rpcResult.getData());
    }

    @GetMapping("/me.json")
    public AjaxResult getMe() {
        SysUser me = authService.getMe();
        return AjaxResult.success(me);
    }

    @GetMapping("/getById.json")
    public AjaxResult getById(String userId) {
        RpcResult<SysUser> userResult = userService.loadSysUserByUserId(RpcRequest.of(userId));
        return AjaxResult.success(userResult.getData());
    }

    @PostMapping("/update.json")
    public AjaxResult update(@RequestBody SysUser user) {
        userService.updateSysUser(RpcRequest.of(user));
        return AjaxResult.success(null);
    }

    @PostMapping("/deleteById.json")
    public AjaxResult deleteById(@RequestBody SysUser user) {
        userService.deleteUser(RpcRequest.of(user.getUserId()));
        return AjaxResult.success(null);
    }

    @PostMapping("/updatePwd.json")
    public AjaxResult updatePwd(@RequestBody SysUser user) {
        UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest();
        updateUserPasswordRequest.setUserId(user.getUserId());
        updateUserPasswordRequest.setPassword(user.getPassword());
        userService.updateUserPassword(RpcRequest.of(updateUserPasswordRequest));
        return AjaxResult.success(null);
    }

    @PostMapping("/updateUserRoles.json")
    public AjaxResult updateUserRoles(@RequestBody UserRoleUpdateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        UpdateUserRoleRequest updateUserRoleRequest = new UpdateUserRoleRequest();
        updateUserRoleRequest.setUserId(request.getUserId());
        updateUserRoleRequest.setRoleIds(request.getRoleIds());
        userService.updateUserRoles(RpcRequest.of(updateUserRoleRequest));
        return AjaxResult.success(null);
    }

    @GetMapping("/list_logins.json")
    public @ResponseBody
    AjaxResult getEncryptKey(@Valid PageRequest request) throws Throwable {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        RpcResult<PageResult<List<LoginInfo>>> logins = tokenService.pageQueryLoginInfo(RpcRequest.of(request));
        return AjaxResult.success(logins.getData());
    }
}
