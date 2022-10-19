package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.user.Permission;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface PermissionRepository {

    String insert(Permission permission);

    Permission findByKey(String key);

    Permission findByPermissionId(String permissionId);

    List<Permission> findByPermissionIds(List<String> permissionIds);

}
