package com.hy.project.demo.service.sso;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.security.SysUser;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public interface TokenService {

    SysUser getUserByToken(HttpServletRequest request);

    String createToken(String userId);

    void cacheToken(String token);

    void cleanToken(String token);
}
