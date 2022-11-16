package com.hy.project.demo.repository.impl;

import java.util.Date;
import java.util.List;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.mybatis.entity.UserRoleRelationDO;
import com.hy.project.demo.mybatis.mapper.UserRoleRelationMapper;
import com.hy.project.demo.repository.UserRoleRelationRepository;
import com.hy.project.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.hy.project.demo.constant.CommonConstants.USER_SYS;

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

        UserRoleRelationDO relationDO = UserRoleRelationDO.of(userId, roleId);
        addCommonParam(relationDO, relationId);

        userRoleRelationMapper.insert(relationDO);
        return relationId;
    }

    @Override
    public void insertAll(List<UserRoleRelationDO> relations) {
        relations.forEach(r -> {
            String relationId = idGenerator.generateId(SequenceNameEnum.SEQ_USER_ROLE_ID);
            addCommonParam(r, relationId);
        });
        userRoleRelationMapper.insertAll(relations);
    }

    @Override
    public List<UserRoleRelationDO> findByUserId(String userId) {
        return userRoleRelationMapper.findByUserId(userId);
    }

    @Override
    public void deleteByUserId(String userId) {
        userRoleRelationMapper.deleteByUserId(userId);
    }

    @Override
    public List<UserRoleRelationDO> findByUserIds(List<String> userIds) {
        return userRoleRelationMapper.findByUserIds(userIds);
    }

    private void addCommonParam(UserRoleRelationDO relation, String relationId) {
        relation.setUserRoleId(relationId);
        relation.setCreateBy(USER_SYS);
        relation.setCreateTime(new Date());
        relation.setUpdateBy(USER_SYS);
        relation.setUpdateTime(new Date());
    }
}
