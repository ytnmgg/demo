package com.hy.project.demo.auth.core.repository.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.hy.project.demo.auth.core.model.SequenceNameEnum;
import com.hy.project.demo.auth.core.mybatis.entity.RolePermissionRelationDO;
import com.hy.project.demo.auth.core.mybatis.mapper.RolePermissionRelationMapper;
import com.hy.project.demo.auth.core.repository.RolePermissionRelationRepository;
import com.hy.project.demo.auth.core.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.hy.project.demo.auth.core.constant.CommonConstants.USER_SYS;

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

        RolePermissionRelationDO relationDO = RolePermissionRelationDO.of(roleId, permissionId);
        addCommonParam(relationDO, relationId);

        rolePermissionRelationMapper.insert(relationDO);

        return relationId;
    }

    @Override
    public void insertAll(List<RolePermissionRelationDO> relations) {
        relations.forEach(r -> {
            String relationId = idGenerator.generateId(SequenceNameEnum.SEQ_ROLE_PERMISSION_ID);
            addCommonParam(r, relationId);
        });
        rolePermissionRelationMapper.insertAll(relations);
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
    public List<RolePermissionRelationDO> findPermissionsByRoleIds(List<String> roleIds) {
        return rolePermissionRelationMapper.findByRoleIds(roleIds);
    }

    @Override
    public void deleteByRoleId(String roleId) {
        rolePermissionRelationMapper.deleteByRoleId(roleId);
    }

    private void addCommonParam(RolePermissionRelationDO relation, String relationId) {
        relation.setRolePermissionId(relationId);
        relation.setCreateBy(USER_SYS);
        relation.setCreateTime(new Date());
        relation.setUpdateBy(USER_SYS);
        relation.setUpdateTime(new Date());
    }
}
