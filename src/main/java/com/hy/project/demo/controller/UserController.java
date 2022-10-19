package com.hy.project.demo.controller;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.controller.request.LoginRequest;
import com.hy.project.demo.controller.request.PageRequest;
import com.hy.project.demo.model.AjaxResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.sso.LoginService;
import com.hy.project.demo.service.user.UserService;
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
        String userId = loginService.register(request.getUsername(), request.getPassword());
        return AjaxResult.success(userId);
    }

    @GetMapping("/me.json")
    public AjaxResult getMe() {
        SysUser me = userService.getMe();
        return AjaxResult.success(me);
    }
}
