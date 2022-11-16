package com.hy.project.demo.service.auth;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.user.Permission;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface PermissionService {

    String createNewPermission(String name, String code);

    PageResult<List<Permission>> pageList(int pageIndex, int pageSize);

    void deletePermission(String id);
}
