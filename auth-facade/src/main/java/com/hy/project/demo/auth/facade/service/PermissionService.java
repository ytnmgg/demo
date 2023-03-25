package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface PermissionService {

    String createNewPermission(String name, String code);

    PageResult<List<Permission>> pageList(int pageIndex, int pageSize);

    void deletePermission(String id);
}
