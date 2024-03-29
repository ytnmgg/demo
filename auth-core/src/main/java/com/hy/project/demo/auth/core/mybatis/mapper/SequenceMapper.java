package com.hy.project.demo.auth.core.mybatis.mapper;

import com.hy.project.demo.auth.core.mybatis.entity.SequenceDO;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public interface SequenceMapper {

    void insert(SequenceDO sequenceDO);

    SequenceDO findByName(String name);

    int compareAndSet(String name, long value, long expect);
}
