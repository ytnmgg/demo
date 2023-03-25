package com.hy.project.demo.auth.core.repository;

import java.util.List;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRepository {

    String insert(SysUser sysUser);

    SysUser findByName(String name);

    SysUser findByUserId(String userId);

    void updateUser(SysUser sysUser);

    SysUser lockByUserId(String userId);

    PageResult<List<SysUser>> pageList(int pageIndex, int pageSize);

    void deleteByUserId(String userId);
}
