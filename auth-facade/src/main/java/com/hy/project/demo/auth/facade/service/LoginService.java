package com.hy.project.demo.auth.facade.service;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.LoginRequest;
import com.hy.project.demo.auth.facade.model.request.RegisterRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;

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
    RpcResult<String> register(RpcRequest<RegisterRequest> request);

    /**
     * 登录
     *
     * @param request req
     * @return result
     */
    RpcResult<String> login(RpcRequest<LoginRequest> request);

    /**
     * 登出
     *
     * @param request 请求
     */
    RpcResult<Void> logout(RpcRequest<SysUser> request);
}
