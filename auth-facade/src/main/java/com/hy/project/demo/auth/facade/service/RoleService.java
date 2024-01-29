package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.Role;
import com.hy.project.demo.auth.facade.model.RoleBase;
import com.hy.project.demo.auth.facade.model.request.CreateNewRoleRequest;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateRolePermissionRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface RoleService {

    RpcResult<String> createNewRole(RpcRequest<CreateNewRoleRequest> request);

    RpcResult<PageResult<List<Role>>> pageList(RpcRequest<PageRequest> request);

    RpcResult<Void> deleteRole(RpcRequest<String> request);

    RpcResult<Void> updateRolePermissions(RpcRequest<UpdateRolePermissionRequest> request);

    RpcResult<List<String>> getPermissions(RpcRequest<List<RoleBase>> request);
}
