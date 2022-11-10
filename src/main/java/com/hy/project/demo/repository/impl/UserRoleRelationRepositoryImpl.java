package com.hy.project.demo.repository.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.mybatis.entity.UserRoleRelationDO;
import com.hy.project.demo.mybatis.mapper.UserRoleRelationMapper;
import com.hy.project.demo.repository.UserRoleRelationRepository;
import com.hy.project.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Repository
public class UserRoleRelationRepositoryImpl implements UserRoleRelationRepository {
    @Autowired
    UserRoleRelationMapper userRoleRelationMapper;

    @Autowired
    IdGenerator idGenerator;

    @Override
    public String insert(String userId, String roleId) {
        String relationId = idGenerator.generateId(SequenceNameEnum.SEQ_USER_ROLE_ID);

        UserRoleRelationDO relationDO = new UserRoleRelationDO();
        relationDO.setUserRoleId(relationId);
        relationDO.setUserId(userId);
        relationDO.setRoleId(roleId);
        relationDO.setCreateBy("admin");
        relationDO.setCreateTime(new Date());
        relationDO.setUpdateBy("admin");
        relationDO.setUpdateTime(new Date());
        relationDO.setRemark(null);

        userRoleRelationMapper.insert(relationDO);

        return relationId;
    }

    @Override
    public List<String> findRolesByUserId(String userId) {
        List<UserRoleRelationDO> relationDoList = userRoleRelationMapper.findByUserId(userId);
        if (null == relationDoList) {
            return null;
        }

        return relationDoList.stream().map(UserRoleRelationDO::getRoleId).collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(String userId) {
        userRoleRelationMapper.deleteByUserId(userId);
    }
}
