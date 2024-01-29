package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.request.CreateNewPermissionRequest;
import com.hy.project.demo.auth.facade.model.request.PageQueryRequest;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface PermissionService {

    RpcResult<String> createNewPermission(RpcRequest<CreateNewPermissionRequest> request);

    RpcResult<PageResult<List<Permission>>> pageList(RpcRequest<PageQueryRequest> request);

    RpcResult<Void> deletePermission(RpcRequest<String> request);
}
