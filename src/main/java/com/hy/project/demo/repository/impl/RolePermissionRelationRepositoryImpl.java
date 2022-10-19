package com.hy.project.demo.repository.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.mybatis.entity.RolePermissionRelationDO;
import com.hy.project.demo.mybatis.mapper.RolePermissionRelationMapper;
import com.hy.project.demo.repository.RolePermissionRelationRepository;
import com.hy.project.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Repository
public class RolePermissionRelationRepositoryImpl implements RolePermissionRelationRepository {
    @Autowired
    RolePermissionRelationMapper rolePermissionRelationMapper;

    @Autowired
    IdGenerator idGenerator;

    @Override
    public String insert(String roleId, String permissionId) {
        String relationId = idGenerator.generateId(SequenceNameEnum.SEQ_ROLE_PERMISSION_ID);

        RolePermissionRelationDO relationDO = new RolePermissionRelationDO();
        relationDO.setRolePermissionId(relationId);
        relationDO.setPermissionId(permissionId);
        relationDO.setRoleId(roleId);
        relationDO.setCreateBy("admin");
        relationDO.setCreateTime(new Date());
        relationDO.setUpdateBy("admin");
        relationDO.setUpdateTime(new Date());
        relationDO.setRemark(null);

        rolePermissionRelationMapper.insert(relationDO);

        return relationId;
    }

    @Override
    public List<String> findPermissionsByRoleId(String roleId) {
        List<RolePermissionRelationDO> relationDoList = rolePermissionRelationMapper.findByRoleId(roleId);
        if (null == relationDoList) {
            return null;
        }
        return relationDoList.stream().map(RolePermissionRelationDO::getPermissionId).collect(Collectors.toList());
    }

    @Override
    public List<String> findPermissionsByRoleIds(List<String> roleIds) {
        List<RolePermissionRelationDO> relationDoList = rolePermissionRelationMapper.findByRoleIds(roleIds);
        if (null == relationDoList) {
            return null;
        }
        return relationDoList.stream().map(RolePermissionRelationDO::getPermissionId).distinct().collect(
            Collectors.toList());
    }
}
