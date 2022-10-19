package com.hy.project.demo.controller.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.controller.request.LoginRequest;
import com.hy.project.demo.model.AjaxResult;
import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.service.sso.LoginService;
import com.hy.project.demo.service.sso.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hy.project.demo.constant.CommonConstants.LOGIN_PAGE_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGOUT_REQUEST_URL;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    RsaService rsaService;

    @Autowired
    private Environment env;

    @RequestMapping(LOGIN_PAGE_URL)
    public String loginPage(Model model) {
        model.addAttribute("front_sso_version", env.getProperty("front.sso.version"));
        return "login";
    }

    @PostMapping(LOGIN_REQUEST_URL)
    public @ResponseBody
    AjaxResult login(@RequestBody LoginRequest request, HttpServletResponse response) throws Throwable {
        String token = loginService.login(request.getUsername(), request.getPassword(), request.getCallback(), response);
        return AjaxResult.success(token);
    }

    @GetMapping("/get_encrypt_key.json")
    public @ResponseBody
    AjaxResult getEncryptKey() throws Throwable {
        String key = rsaService.getRsaPublicKeyString();
        return AjaxResult.success(key);
    }

    @PostMapping(LOGOUT_REQUEST_URL)
    public @ResponseBody
    DemoResult<Void> logout(HttpServletRequest httpReq) {
        loginService.logout(httpReq);
        return DemoResult.buildSuccessResult(null);
    }

}
