package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.auth.facade.model.SysUser;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
public interface UserService {

    SysUser loadSysUserByName(String name);

    SysUser loadSysUserByUserId(String userId);

    void updateSysUser(SysUser sysUser);

    void touchUser(SysUser user);

    SysUser touchUser(String userId);

    SysUser getCacheUser(String userId);

    SysUser getMe();

    void clearUser(String userId);

    void deleteUser(String userId);

    void updateUserPassword(String userId, String newPwd);

    void updateUserRoles(String userId, List<String> roleIds);

    String createNewUser(String name, String password);

    /**
     * 查询用户ØØ列表
     *
     * @param pageIndex index
     * @param pageSize  页码
     * @return 结果
     */
    PageResult<List<SysUser>> pageListUsers(int pageIndex, int pageSize);
}
