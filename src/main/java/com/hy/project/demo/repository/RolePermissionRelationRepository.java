package com.hy.project.demo.repository;

import java.util.List;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface RolePermissionRelationRepository {

    String insert(String roleId, String permissionId);

    List<String> findPermissionsByRoleId(String roleId);

    List<String> findPermissionsByRoleIds(List<String> roleIds);

}
