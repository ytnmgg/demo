package com.hy.project.demo.repository;

import java.util.List;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRoleRelationRepository {

    String insert(String userId, String roleId);

    List<String> findRolesByUserId(String userId);

    void deleteByUserId(String userId);
}
