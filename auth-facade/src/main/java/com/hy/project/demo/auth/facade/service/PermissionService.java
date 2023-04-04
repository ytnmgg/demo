package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.request.CreateNewPermissionRequest;
import com.hy.project.demo.auth.facade.model.request.PageQueryRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public interface PermissionService {

    SimpleResult<String> createNewPermission(CreateNewPermissionRequest request);

    PageResult<List<Permission>> pageList(PageQueryRequest request);

    BaseResult deletePermission(SimpleRequest<String> request);
}
