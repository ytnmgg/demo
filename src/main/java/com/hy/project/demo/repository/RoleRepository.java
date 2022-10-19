package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.user.Role;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface RoleRepository {

    String insert(Role role);

    Role findByKey(String key);

    Role findByRoleId(String roleId);

    List<Role> findByRoleIds(List<String> roleIds);
}
