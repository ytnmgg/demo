package com.hy.project.demo.mybatis.mapper;

import java.util.List;

import com.hy.project.demo.mybatis.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface UserMapper extends CrudMapper<UserDO, String> {

    List<UserDO> findByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    UserDO findByName(String name);

    UserDO lockById(String id);
}
