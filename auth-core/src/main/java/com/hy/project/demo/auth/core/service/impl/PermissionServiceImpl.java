package com.hy.project.demo.auth.core.service.impl;

import java.util.List;

import com.hy.project.demo.auth.core.repository.PermissionRepository;
import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.request.CreateNewPermissionRequest;
import com.hy.project.demo.auth.facade.model.request.PageQueryRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.auth.facade.service.PermissionService;
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
    public RpcResult<String> createNewPermission(RpcRequest<CreateNewPermissionRequest> request) {
        Permission permission = new Permission();
        permission.setPermissionName(request.getData().getName());
        permission.setPermissionKey(request.getData().getCode());
        return RpcResult.success(permissionRepository.insert(permission));
    }

    @Override
    public RpcResult<PageResult<List<Permission>>> pageList(RpcRequest<PageQueryRequest> request) {
        return RpcResult.success(
            permissionRepository.pageList(request.getData().getPageIndex(), request.getData().getPageSize()));
    }

    @Override
    public RpcResult<Void> deletePermission(RpcRequest<String> request) {
        permissionRepository.deleteById(request.getData());
        return RpcResult.success(null);
    }
}
