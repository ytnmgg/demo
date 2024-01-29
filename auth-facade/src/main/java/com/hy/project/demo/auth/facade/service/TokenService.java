package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public interface TokenService {

    RpcResult<SysUser> getUserByToken(RpcRequest<String> request);

    RpcResult<String> createToken(RpcRequest<String> request);

    RpcResult<Void> removeToken(RpcRequest<String> request);

    RpcResult<Void> saveToken(RpcRequest<LoginInfo> request);

    RpcResult<Void> expireTokens(RpcRequest<Void> request);

    RpcResult<Void> touchToken(RpcRequest<String> request);

    RpcResult<PageResult<List<LoginInfo>>> pageQueryLoginInfo(RpcRequest<PageRequest> request);
}
