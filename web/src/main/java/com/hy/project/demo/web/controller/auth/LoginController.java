package com.hy.project.demo.web.controller.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.auth.facade.service.LoginService;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.web.model.AjaxResult;
import com.hy.project.demo.web.model.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hy.project.demo.web.constant.WebConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.web.constant.WebConstants.LOGOUT_REQUEST_URL;

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

    //@RequestMapping(LOGIN_PAGE_URL)
    //public String loginPage(Model model) {
    //    model.addAttribute("front_sso_version", env.getProperty("front.sso.version"));
    //    return "login";
    //}

    @PostMapping(LOGIN_REQUEST_URL)
    public @ResponseBody
    AjaxResult login(@RequestBody LoginRequest req, HttpServletRequest request, HttpServletResponse response)
        throws Throwable {
        String token = loginService.login(req.getUsername(), req.getPassword(), req.getCallback(),
            request, response);
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
    AjaxResult logout(HttpServletRequest httpReq) {
        loginService.logout(httpReq);
        return AjaxResult.success(null);
    }
}