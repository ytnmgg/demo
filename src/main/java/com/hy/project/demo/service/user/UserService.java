package com.hy.project.demo.service.user;

import java.util.List;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.security.SysUser;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
public interface UserService {

    /**
     * 查找用户
     *
     * @param uid uid
     * @return 结果
     */
    User getUserById(String uid);

    SysUser loadSysUserByName(String name);

    SysUser loadSysUserByUserId(String userId);
    /**
     * 创建新用户
     *
     * @param name     name
     * @param password password
     * @param role
     */
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
