package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewUserRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserPasswordRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserRoleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
public interface UserService {

    SimpleResult<SysUser> loadSysUserByName(SimpleRequest<String> request);

    SimpleResult<SysUser> loadSysUserByUserId(SimpleRequest<String> request);

    BaseResult updateSysUser(SimpleRequest<SysUser> request);

    BaseResult touchUser(SimpleRequest<SysUser> request);

    SimpleResult<SysUser> touchUserById(SimpleRequest<String> request);

    SimpleResult<SysUser> getCacheUser(SimpleRequest<SysUser> request);

    BaseResult clearUser(SimpleRequest<String> request);

    BaseResult deleteUser(SimpleRequest<String> request);

    BaseResult updateUserPassword(UpdateUserPasswordRequest request);

    BaseResult updateUserRoles(UpdateUserRoleRequest request);

    SimpleResult<String> createNewUser(CreateNewUserRequest request);

    PageResult<List<SysUser>> pageListUsers(PageRequest request);
}
