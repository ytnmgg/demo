package com.hy.project.demo.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.model.user.Permission;
import com.hy.project.demo.mybatis.entity.PermissionDO;
import com.hy.project.demo.mybatis.mapper.PermissionMapper;
import com.hy.project.demo.repository.PermissionRepository;
import com.hy.project.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Repository
public class PermissionRepositoryImpl implements PermissionRepository {
    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    IdGenerator idGenerator;

    @Override
    public String insert(Permission permission) {
        PermissionDO permissionDO = toDo(permission);
        String permissionId = idGenerator.generateId(SequenceNameEnum.SEQ_PERMISSION_ID);
        permissionDO.setPermissionId(permissionId);
        permissionMapper.insert(permissionDO);
        return permissionId;
    }

    @Override
    public Permission findByKey(String key) {
        PermissionDO permissionDO = permissionMapper.findByKey(key);
        return null == permissionDO ? null : toModel(permissionDO);
    }

    @Override
    public Permission findByPermissionId(String permissionId) {
        PermissionDO permissionDO = permissionMapper.findById(permissionId);
        return null == permissionDO ? null : toModel(permissionDO);
    }

    @Override
    public List<Permission> findByPermissionIds(List<String> permissionIds) {
        Iterable<PermissionDO> permissionDoList = permissionMapper.findAllById(permissionIds);
        if (null == permissionDoList) {
            return null;
        }

        List<Permission> result = new ArrayList<>();
        permissionDoList.forEach((item) -> result.add(toModel(item)));

        return result;
    }

    private PermissionDO toDo(Permission permission) {
        if (null == permission) {
            return null;
        }

        PermissionDO permissionDO = new PermissionDO();
        permissionDO.setPermissionId(permission.getPermissionId());
        permissionDO.setPermissionName(permission.getPermissionName());
        permissionDO.setPermissionKey(permission.getPermissionKey());
        permissionDO.setStatus("0");
        permissionDO.setDelFlag("0");
        permissionDO.setCreateBy("admin");
        permissionDO.setCreateTime(new Date());
        permissionDO.setUpdateBy("admin");
        permissionDO.setUpdateTime(new Date());
        permissionDO.setRemark(null);
        return permissionDO;
    }

    private Permission toModel(PermissionDO permissionDO) {
        if (null == permissionDO) {
            return null;
        }

        Permission permission = new Permission();
        permission.setPermissionId(permissionDO.getPermissionId());
        permission.setPermissionName(permissionDO.getPermissionName());
        permission.setPermissionKey(permissionDO.getPermissionKey());
        return permission;
    }
}
