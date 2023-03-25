package com.hy.project.demo.auth.core.service.impl;

import java.util.List;

import com.hy.project.demo.auth.core.repository.PermissionRepository;
import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.service.PermissionService;
import com.hy.project.demo.common.model.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public String createNewPermission(String name, String code) {
        Permission permission = new Permission();
        permission.setPermissionName(name);
        permission.setPermissionKey(code);
        return permissionRepository.insert(permission);
    }

    @Override
    public PageResult<List<Permission>> pageList(int pageIndex, int pageSize) {
        return permissionRepository.pageList(pageIndex, pageSize);
    }

    @Override
    public void deletePermission(String id) {
        permissionRepository.deleteById(id);
    }
}
