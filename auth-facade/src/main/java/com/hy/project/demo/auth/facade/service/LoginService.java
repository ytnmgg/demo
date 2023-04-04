package com.hy.project.demo.auth.facade.service;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.LoginRequest;
import com.hy.project.demo.auth.facade.model.request.RegisterRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseResult;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
public interface LoginService {

    /**
     * 注册用户
     *
     * @param request request
     * @return 结果
     */
    SimpleResult<String> register(RegisterRequest request);

    /**
     * 登录
     *
     * @param request req
     * @return result
     */
    SimpleResult<String> login(LoginRequest request);

    /**
     * 登出
     *
     * @param request 请求
     */
    BaseResult logout(SimpleRequest<SysUser> request);
}
