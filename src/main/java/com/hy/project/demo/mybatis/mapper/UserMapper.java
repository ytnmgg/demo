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
public interface UserMapper {

    /**
     * 获取全部用户
     *
     * @param type type
     * @param name name
     * @return 结果
     */
    List<UserDO> list(@Param("type") String type, @Param("name") String name);
}
