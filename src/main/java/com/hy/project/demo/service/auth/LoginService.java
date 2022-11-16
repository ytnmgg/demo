package com.hy.project.demo.service.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
public interface LoginService {

    /**
     * 注册用户
     *
     * @param name     name
     * @param password password
     */
    String register(String name, String password);

    /**
     * 登录
     *
     * @param name     name
     * @param password password
     * @param callback callback
     * @param response response
     */
    String login(String name, String password, String callback, HttpServletResponse response) throws Throwable;

    /**
     * 登出
     *
     * @param request 请求
     */
    void logout(HttpServletRequest request);
}
