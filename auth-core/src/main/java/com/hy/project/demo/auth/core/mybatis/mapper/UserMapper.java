package com.hy.project.demo.auth.core.mybatis.mapper;

import com.hy.project.demo.auth.core.mybatis.entity.UserDO;
import com.hy.project.demo.common.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface UserMapper extends CrudMapper<UserDO, String> {

    UserDO findByName(String name);

    UserDO lockById(String id);
}
