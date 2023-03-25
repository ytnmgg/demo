package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.LoginUser;
import com.hy.project.demo.auth.facade.model.Role;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface RoleService {

    String createNewRole(String name, String code, List<String> permissionIds);

    PageResult<List<Role>> pageList(int pageIndex, int pageSize);

    void deleteRole(String id);

    void updateRolePermissions(String roleId, List<String> permissionIds);

    LoginUser buildLoginUser(SysUser user);
}
