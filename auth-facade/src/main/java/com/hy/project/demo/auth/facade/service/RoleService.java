package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.LoginUser;
import com.hy.project.demo.auth.facade.model.Role;
import com.hy.project.demo.auth.facade.model.RoleBase;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewRoleRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateRolePermissionRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface RoleService {

    SimpleResult<String> createNewRole(CreateNewRoleRequest request);

    PageResult<List<Role>> pageList(PageRequest request);

    BaseResult deleteRole(SimpleRequest<String> request);

    BaseResult updateRolePermissions(UpdateRolePermissionRequest request);

    SimpleResult<List<String>> getPermissions(SimpleRequest<List<RoleBase>> request);
}
