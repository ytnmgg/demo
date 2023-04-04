package com.hy.project.demo.web.service.impl;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.web.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2023/04/03
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public SysUser getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && !(authentication.getPrincipal() instanceof SysUser)) {
            return null;
        }
        return (SysUser)authentication.getPrincipal();
    }
}
