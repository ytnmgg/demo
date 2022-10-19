package com.hy.project.demo.mybatis.mapper;

import com.hy.project.demo.mybatis.entity.PermissionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface PermissionMapper extends CrudMapper<PermissionDO, String> {

    PermissionDO findByKey(String key);

    PermissionDO lockById(String id);
}
