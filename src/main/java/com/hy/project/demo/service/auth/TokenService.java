package com.hy.project.demo.service.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.controller.request.PageRequest;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.LoginInfo;
import com.hy.project.demo.security.SysUser;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public interface TokenService {

    SysUser getUserByToken(HttpServletRequest request);

    String createToken(String userId);

    void removeToken(String token);

    void saveToken(LoginInfo loginInfo);

    void expireTokens();

    void touchToken(String token);

    PageResult<List<LoginInfo>> pageQueryLoginInfo(PageRequest request);
}
