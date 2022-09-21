package com.hy.project.demo.mybatis.mapper;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public interface CrudMapper<DO, ID> {
    void insert(DO entity);

    void insertAll(Iterable<DO> entities);

    void update(DO entity);

    void updateAll(Iterable<DO> entities);

    DO findById(ID id);

    Iterable<DO> findAllById(Iterable<ID> ids);

    Iterable<DO> findAll();

    long count();

    void deleteById(ID id);

    void deleteAllById(Iterable<ID> ids);

    void deleteAll();
}
