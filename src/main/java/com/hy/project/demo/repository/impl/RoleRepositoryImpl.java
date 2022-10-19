package com.hy.project.demo.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.model.user.Role;
import com.hy.project.demo.mybatis.entity.RoleDO;
import com.hy.project.demo.mybatis.mapper.RoleMapper;
import com.hy.project.demo.repository.RoleRepository;
import com.hy.project.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    @Autowired
    RoleMapper roleMapper;

    @Autowired
    IdGenerator idGenerator;

    @Override
    public String insert(Role role) {
        RoleDO roleDO = toDo(role);
        String roleId = idGenerator.generateId(SequenceNameEnum.SEQ_ROLE_ID);
        roleDO.setRoleId(roleId);
        roleMapper.insert(roleDO);
        return roleId;
    }

    @Override
    public Role findByKey(String key) {
        RoleDO roleDO = roleMapper.findByKey(key);
        return null == roleDO ? null : toModel(roleDO);
    }

    @Override
    public Role findByRoleId(String roleId) {
        RoleDO roleDO = roleMapper.findById(roleId);
        return null == roleDO ? null : toModel(roleDO);
    }

    @Override
    public List<Role> findByRoleIds(List<String> roleIds) {
        List<RoleDO> roleDoList = roleMapper.findAllById(roleIds);
        if (null == roleDoList) {
            return null;
        }

        List<Role> result = new ArrayList<>();
        roleDoList.forEach((item) -> result.add(toModel(item)));

        return result;
    }

    private RoleDO toDo(Role role) {
        if (null == role) {
            return null;
        }

        RoleDO roleDO = new RoleDO();
        roleDO.setRoleId(role.getRoleId());
        roleDO.setRoleName(role.getRoleName());
        roleDO.setRoleKey(role.getRoleKey());
        roleDO.setStatus("0");
        roleDO.setDelFlag("0");
        roleDO.setCreateBy("admin");
        roleDO.setCreateTime(new Date());
        roleDO.setUpdateBy("admin");
        roleDO.setUpdateTime(new Date());
        roleDO.setRemark(null);
        return roleDO;
    }

    private Role toModel(RoleDO roleDO) {
        if (null == roleDO) {
            return null;
        }

        Role role = new Role();
        role.setRoleId(roleDO.getRoleId());
        role.setRoleName(roleDO.getRoleName());
        role.setRoleKey(roleDO.getRoleKey());
        return role;
    }
}
