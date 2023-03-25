package com.hy.project.demo.web.controller.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.web.model.GrafanaAuthTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static com.hy.project.demo.web.util.WebUtil.getTokenFromCookie;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@RestController
@RequestMapping("/grafana-auth")
public class GrafanaAuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrafanaAuthController.class);

    @Autowired
    UserService userService;

    /**
     * grafana oauth第一步
     * grafana通过浏览器链接访问应用的auth接口，期望：
     * 1. 拿到授权码（比如用微信号去登录第三方应用，第三方应用（类比grafana）会跳转到微信（类比demoapp），微信应用本身会弹窗让用户确认是否授权微信号登录第三方应用）
     * 2. 应用接口redirect回grafana的generic_oauth接口，并在url带上grafana传入的state和demoapp生成的授权码code
     *
     * 注意：这一步是在浏览器里面直接发起访问的，所以grafana配置的auth_url是一个可以从浏览器发起的地址（比如相对地址、或外网域名地址）
     * 为什么要从浏览器发起访问，而不是grafana直接内部调用？因为这样可以带入用户的cookie信息，帮助demoapp鉴定是谁发起的访问
     *
     * @param request
     * @param response
     * @return
     * @throws Throwable
     */
    @GetMapping("/auth.json")
    public @ResponseBody
    ModelAndView auth(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String state = request.getParameter("state");

        // 本来应该返回一个授权码之类的，和当前登录用户做映射，这里简化处理，至今返回token，有安全问题，因为token是放到url里面传递的
        String code = getTokenFromCookie(request);

        String url = String.format("/grafana/login/generic_oauth?state=%s&code=%s", state, code);
        response.sendRedirect(url);
        return null;
    }

    /**
     * grafana oauth第二步
     * grafana内部调用应用的token接口，期望：
     * 1. 通过授权码code，拿到用户的token信息
     *
     * 在这一步，demoapp需要鉴权：
     * 1. 看授权码code是否真实（这里简化了，直接用的token）
     *
     * 注意：这一步没有cookie，也没有header，是内部调用，所以需要加入免鉴权白名单：com.hy.project.demo.util.SsoUtil#ESCAPE_PATH
     *
     * @param req
     * @return
     * @throws Throwable
     */
    @PostMapping("/token.json")
    public @ResponseBody
    JSONObject token(GrafanaAuthTokenRequest req)
        throws Throwable {
        JSONObject result = new JSONObject();
        result.put("access_token", req.getCode());
        result.put("token_type", "Bearer");
        return result;
    }

    /**
     * grafana oauth第三步
     * grafana内部调用应用的api接口，期望：
     * 1. 通过token，拿到用户信息
     *
     *
     * 注意：这一步grafana的调用会带上鉴权header：
     * key=authorization, value=Bearer ${token}
     * 所以不需要加白，自然可以通过demoapp的鉴权
     *
     * @return
     * @throws Throwable
     */
    @GetMapping("/api.json")
    public @ResponseBody
    JSONObject api() throws Throwable {

        // 下面是用系统登录用户去登录grafana，grafana如果没找到，会自动创建用户
        //SysUser sysUser = userService.getMe();
        //String name = sysUser.getUserName();
        //String email = sysUser.getEmail();
        //if (StringUtils.isBlank(email)) {
        //    email = String.format("%s@demoapp.com", name);
        //}
        //
        //JSONObject result = new JSONObject();
        //result.put("name", name);
        //result.put("email", email);
        //result.put("role", "Viewer");

        // 为了方便图表的权限管理，暂使用同一个手工预置好的角色，每个人查看grafana都是这个用户
        JSONObject result = new JSONObject();
        result.put("name", "metricViewer");
        result.put("email", "metricViewer@demoapp.com");

        return result;
    }
}
