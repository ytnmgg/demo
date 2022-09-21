package com.hy.project.demo.service.sso;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.sso.Account;

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

    /**
     * 获取账号信息
     *
     * @param token token
     * @return 结果
     */
    Account getAccountByToken(String token);

    /**
     * 刷新token时间
     *
     * @param token token
     */
    void refreshToken(String token);
}
