package com.hy.project.demo.controller;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.controller.request.LoginRequest;
import com.hy.project.demo.controller.request.PageRequest;
import com.hy.project.demo.controller.request.UserRoleUpdateRequest;
import com.hy.project.demo.model.AjaxResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.auth.LoginService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.ResultUtil;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    LoginService loginService;

    @GetMapping("/list.json")
    public AjaxResult listUsers(@Valid PageRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        PageResult<List<SysUser>> result = userService.pageListUsers(request.getPageIndex(), request.getPageSize());
        return AjaxResult.success(result);
    }

    @PostMapping("/register.json")
    public AjaxResult register(@RequestBody LoginRequest request) {
        // mock
        return AjaxResult.fail(ResultUtil.CODE_403, "暂不支持");

        // 权限管理完善前，注册功能暂时不开放
        //String userId = loginService.register(request.getUsername(), request.getPassword());
        //return AjaxResult.success(userId);
    }

    @GetMapping("/me.json")
    public AjaxResult getMe() {
        SysUser me = userService.getMe();
        return AjaxResult.success(me);
    }

    @GetMapping("/getById.json")
    public AjaxResult getById(String userId) {
        SysUser user = userService.loadSysUserByUserId(userId);
        return AjaxResult.success(user);
    }

    @PostMapping("/update.json")
    public AjaxResult update(@RequestBody SysUser user) {
        userService.updateSysUser(user);
        return AjaxResult.success(null);
    }

    @PostMapping("/deleteById.json")
    public AjaxResult deleteById(@RequestBody SysUser user) {
        userService.deleteUser(user.getUserId());
        return AjaxResult.success(null);
    }

    @PostMapping("/updatePwd.json")
    public AjaxResult updatePwd(@RequestBody SysUser user) {
        userService.updateUserPassword(user.getUserId(), user.getPassword());
        return AjaxResult.success(null);
    }

    @PostMapping("/updateUserRoles.json")
    public AjaxResult updateUserRoles(@RequestBody UserRoleUpdateRequest request) {
        AssertUtil.notNull(request, INVALID_PARAM_EXCEPTION, "request can not be null");
        userService.updateUserRoles(request.getUserId(), request.getRoleIds());
        return AjaxResult.success(null);
    }

}
