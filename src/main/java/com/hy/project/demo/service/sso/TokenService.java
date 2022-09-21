package com.hy.project.demo.service.sso;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.security.LoginUser;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public interface TokenService {

    LoginUser getLoginUser(HttpServletRequest request);

    String createToken(String userId);
}
