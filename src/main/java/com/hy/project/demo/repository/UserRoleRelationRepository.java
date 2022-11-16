package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.mybatis.entity.UserRoleRelationDO;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRoleRelationRepository {

    String insert(String userId, String roleId);

    void insertAll(List<UserRoleRelationDO> relations);

    List<UserRoleRelationDO> findByUserId(String userId);

    void deleteByUserId(String userId);

    List<UserRoleRelationDO> findByUserIds(List<String> userIds);

}
