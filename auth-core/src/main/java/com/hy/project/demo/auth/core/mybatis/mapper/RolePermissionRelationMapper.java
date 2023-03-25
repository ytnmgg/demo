package com.hy.project.demo.auth.core.mybatis.mapper;

import java.util.List;

import com.hy.project.demo.auth.core.mybatis.entity.RolePermissionRelationDO;
import com.hy.project.demo.common.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface RolePermissionRelationMapper extends CrudMapper<RolePermissionRelationDO, String> {

    List<RolePermissionRelationDO> findByRoleId(String roleId);

    List<RolePermissionRelationDO> findByRoleIds(List<String> roleIds);

    void deleteByRoleId(String roleId);
}
