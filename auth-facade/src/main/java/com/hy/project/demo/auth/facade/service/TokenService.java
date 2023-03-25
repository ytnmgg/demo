package com.hy.project.demo.auth.facade.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

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
