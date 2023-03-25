package com.hy.project.demo.auth.core.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hy.project.demo.auth.core.model.SequenceNameEnum;
import com.hy.project.demo.auth.core.mybatis.entity.RoleDO;
import com.hy.project.demo.auth.core.mybatis.mapper.RoleMapper;
import com.hy.project.demo.auth.core.repository.RoleRepository;
import com.hy.project.demo.auth.core.util.IdGenerator;
import com.hy.project.demo.auth.facade.model.RoleBase;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import static com.hy.project.demo.common.util.DateUtil.STANDARD_STR;

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
    public String insert(RoleBase role) {
        RoleDO roleDO = toDo(role);
        String roleId = idGenerator.generateId(SequenceNameEnum.SEQ_ROLE_ID);
        roleDO.setRoleId(roleId);
        roleMapper.insert(roleDO);
        return roleId;
    }

    @Override
    public RoleBase findByKey(String key) {
        RoleDO roleDO = roleMapper.findByKey(key);
        return null == roleDO ? null : toModel(roleDO);
    }

    @Override
    public RoleBase findByRoleId(String roleId) {
        RoleDO roleDO = roleMapper.findById(roleId);
        return null == roleDO ? null : toModel(roleDO);
    }

    @Override
    public RoleBase lockByRoleId(String roleId) {
        RoleDO roleDO = roleMapper.lockById(roleId);
        return null == roleDO ? null : toModel(roleDO);
    }

    @Override
    public List<RoleBase> findByRoleIds(List<String> roleIds) {
        List<RoleDO> roleDoList = roleMapper.findAllById(roleIds);
        if (null == roleDoList) {
            return null;
        }

        List<RoleBase> result = new ArrayList<>();
        roleDoList.forEach((item) -> result.add(toModel(item)));

        return result;
    }

    @Override
    public PageResult<List<RoleBase>> pageList(int pageIndex, int pageSize) {
        long total = roleMapper.count();

        PageResult<List<RoleBase>> result = PageResult.of(Lists.newArrayList(), pageIndex, pageSize, total);

        if (total == 0) {
            return result;
        }

        int offset = (pageIndex - 1) * pageSize;
        List<RoleDO> roleDOS = roleMapper.findByPage(offset, pageSize);

        if (CollectionUtils.isEmpty(roleDOS)) {
            return result;
        }

        result.setData(roleDOS.stream().map(this::toModel).filter(Objects::nonNull).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void deleteById(String id) {
        roleMapper.deleteById(id);
    }

    private RoleDO toDo(RoleBase role) {
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

    private RoleBase toModel(RoleDO roleDO) {
        if (null == roleDO) {
            return null;
        }

        RoleBase role = new RoleBase();
        role.setRoleId(roleDO.getRoleId());
        role.setRoleName(roleDO.getRoleName());
        role.setRoleKey(roleDO.getRoleKey());

        if (null != roleDO.getCreateTime()) {
            role.setCreateTime(DateUtil.format(roleDO.getCreateTime(), STANDARD_STR));
        }
        return role;
    }
}
