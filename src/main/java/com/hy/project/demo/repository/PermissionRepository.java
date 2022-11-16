package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.user.Permission;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Repository
public interface PermissionRepository {

    String insert(Permission permission);

    Permission findByKey(String key);

    Permission findByPermissionId(String permissionId);

    List<Permission> findByPermissionIds(List<String> permissionIds);

    PageResult<List<Permission>> pageList(int pageIndex, int pageSize);

    void deleteById(String id);
}
