package com.hy.project.demo.web.controller.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.LoginRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.auth.facade.service.LoginService;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.common.model.AjaxResult;
import com.hy.project.demo.common.util.ServletUtil;
import com.hy.project.demo.web.model.LoginWebRequest;
import com.hy.project.demo.web.service.AuthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.LOGIN_FAIL;
import static com.hy.project.demo.web.constant.WebConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.web.constant.WebConstants.LOGOUT_REQUEST_URL;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Controller
public class LoginController {

    @DubboReference
    LoginService loginService;

    @DubboReference
    RsaService rsaService;

    @Autowired
    private Environment env;

    @Autowired
    AuthService authService;

    //@RequestMapping(LOGIN_PAGE_URL)
    //public String loginPage(Model model) {
    //    model.addAttribute("front_sso_version", env.getProperty("front.sso.version"));
    //    return "login";
    //}

    @PostMapping(LOGIN_REQUEST_URL)
    public @ResponseBody
    AjaxResult login(@RequestBody LoginWebRequest req, HttpServletRequest request, HttpServletResponse response) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName(req.getUsername());
        loginRequest.setPassword(req.getPassword());
        loginRequest.setClientIp(ServletUtil.getClientIP(request));
        loginRequest.setUserAgent(ServletUtil.getUserAgent(request));

        RpcResult<String> loginResult = loginService.login(RpcRequest.of(loginRequest));

        if (loginResult.success()) {
            return AjaxResult.success(loginResult.getData());
        } else {
            return AjaxResult.fail(LOGIN_FAIL);
        }
    }

    @GetMapping("/get_encrypt_key.json")
    public @ResponseBody
    AjaxResult getEncryptKey() throws Throwable {
        RpcResult<String> result = rsaService.getRsaPublicKeyString(RpcRequest.of(null));
        return AjaxResult.success(result.getData());
    }

    @PostMapping(LOGOUT_REQUEST_URL)
    public @ResponseBody
    AjaxResult logout(HttpServletRequest httpReq) {
        SysUser user = authService.getMe();
        loginService.logout(RpcRequest.of(user));
        return AjaxResult.success(null);
    }
}
