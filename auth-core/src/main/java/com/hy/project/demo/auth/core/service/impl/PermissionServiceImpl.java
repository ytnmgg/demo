package com.hy.project.demo.auth.core.service.impl;

import java.util.List;

import com.hy.project.demo.auth.core.repository.PermissionRepository;
import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.request.CreateNewPermissionRequest;
import com.hy.project.demo.auth.facade.model.request.PageQueryRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.auth.facade.service.PermissionService;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
@Service
@DubboService
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public SimpleResult<String> createNewPermission(CreateNewPermissionRequest request) {
        Permission permission = new Permission();
        permission.setPermissionName(request.getName());
        permission.setPermissionKey(request.getCode());
        return SimpleResult.of(permissionRepository.insert(permission));
    }

    @Override
    public PageResult<List<Permission>> pageList(PageQueryRequest request) {
        return permissionRepository.pageList(request.getPageIndex(), request.getPageSize());
    }

    @Override
    public BaseResult deletePermission(SimpleRequest<String> request) {
        permissionRepository.deleteById(request.getData());
        return new BaseResult();
    }
}
