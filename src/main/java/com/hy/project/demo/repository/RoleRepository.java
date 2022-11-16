package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.user.RoleBase;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface RoleRepository {

    String insert(RoleBase role);

    RoleBase findByKey(String key);

    RoleBase findByRoleId(String roleId);

    RoleBase lockByRoleId(String roleId);

    List<RoleBase> findByRoleIds(List<String> roleIds);

    PageResult<List<RoleBase>> pageList(int pageIndex, int pageSize);

    void deleteById(String id);
}
