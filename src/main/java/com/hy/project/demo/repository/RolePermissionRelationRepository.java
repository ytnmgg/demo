package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.mybatis.entity.RolePermissionRelationDO;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface RolePermissionRelationRepository {

    String insert(String roleId, String permissionId);

    void insertAll(List<RolePermissionRelationDO> relations);

    List<String> findPermissionsByRoleId(String roleId);

    List<RolePermissionRelationDO> findPermissionsByRoleIds(List<String> roleIds);

    void deleteByRoleId(String roleId);
}
