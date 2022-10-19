package com.hy.project.demo.mybatis.mapper;

import java.util.List;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public interface CrudMapper<DO, ID> {
    void insert(DO entity);

    void insertAll(List<DO> entities);

    void update(DO entity);

    void updateAll(List<DO> entities);

    DO findById(ID id);

    List<DO> findAllById(List<ID> ids);

    List<DO> findAll();

    long count();

    void deleteById(ID id);

    void deleteAllById(List<ID> ids);

    void deleteAll();
}
