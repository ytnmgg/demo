package com.hy.project.demo.web.service;

import com.hy.project.demo.auth.facade.model.SysUser;

/**
 * @author rick.wl
 * @date 2023/04/03
 */
public interface AuthService {
    SysUser getMe();
}
