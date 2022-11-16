package com.hy.project.demo.service.auth;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.user.Role;
import com.hy.project.demo.security.LoginUser;
import com.hy.project.demo.security.SysUser;

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
