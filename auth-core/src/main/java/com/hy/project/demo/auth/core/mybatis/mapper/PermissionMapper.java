package com.hy.project.demo.auth.core.mybatis.mapper;

import com.hy.project.demo.auth.core.mybatis.entity.PermissionDO;
import com.hy.project.demo.common.mapper.CrudMapper;
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
