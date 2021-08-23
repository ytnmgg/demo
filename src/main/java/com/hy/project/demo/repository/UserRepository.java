package com.hy.project.demo.repository;

import java.util.List;

import com.hy.project.demo.model.User;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public interface UserRepository {

    /**
     * 查询所有用户
     *
     * @param type 类型
     * @param name name
     * @return 结果
     */
    List<User> list(String type, String name);
}
