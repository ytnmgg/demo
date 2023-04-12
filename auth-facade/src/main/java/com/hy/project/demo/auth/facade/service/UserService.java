package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewUserRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserPasswordRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserRoleRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
public interface UserService {

    RpcResult<SysUser> loadSysUserByName(RpcRequest<String> request);

    RpcResult<SysUser> loadSysUserByUserId(RpcRequest<String> request);

    RpcResult<Void> updateSysUser(RpcRequest<SysUser> request);

    RpcResult<Void> touchUser(RpcRequest<SysUser> request);

    RpcResult<SysUser> touchUserById(RpcRequest<String> request);

    RpcResult<SysUser> getCacheUser(RpcRequest<SysUser> request);

    RpcResult<Void> clearUser(RpcRequest<String> request);

    RpcResult<Void> deleteUser(RpcRequest<String> request);

    RpcResult<Void> updateUserPassword(RpcRequest<UpdateUserPasswordRequest> request);

    RpcResult<Void> updateUserRoles(RpcRequest<UpdateUserRoleRequest> request);

    RpcResult<String> createNewUser(RpcRequest<CreateNewUserRequest> request);

    RpcResult<PageResult<List<SysUser>>> pageListUsers(RpcRequest<PageRequest> request);
}
