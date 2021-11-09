package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.sso.User;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRepository {

    /**
     * 插入
     * @param user user
     */
    void insert(User user);

    /**
     * 查询
     * @param uid uid
     * @return 结果
     */
    User getById(String uid);

    /**
     * 查询
     * @param name name
     * @return 结果
     */
    User getByName(String name);

    /**
     * 查询所有用户
     *
     * @param role 类型
     * @param status status
     * @param name name
     * @return 结果
     */
    List<User> list(String role, String status, String name);
}
