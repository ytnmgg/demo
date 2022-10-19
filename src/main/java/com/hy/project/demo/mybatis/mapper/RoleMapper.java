package com.hy.project.demo.mybatis.mapper;

import com.hy.project.demo.mybatis.entity.RoleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface RoleMapper extends CrudMapper<RoleDO, String> {

    RoleDO findByKey(String key);

    RoleDO lockById(String id);
}
