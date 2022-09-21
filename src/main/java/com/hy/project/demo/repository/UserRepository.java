package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.security.SysUser;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRepository {

    String insert(SysUser sysUser);

    SysUser findByName(String name);

    SysUser findByUserId(String userId);

    /**
     * 查询
     *
     * @param uid uid
     * @return 结果
     */
    User getById(String uid);

    /**
     * 查询
     *
     * @param name name
     * @return 结果
     */
    User getByName(String name);

    /**
     * 查询所有用户
     *
     * @param role   类型
     * @param status status
     * @param name   name
     * @return 结果
     */
    List<User> list(String role, String status, String name);

    /**
     * 列表查询
     *
     * @param pageIndex 页码
     * @param pageSize  单页大小
     * @return 结果
     */
    PageResult<List<SysUser>> pageList(int pageIndex, int pageSize);
}
