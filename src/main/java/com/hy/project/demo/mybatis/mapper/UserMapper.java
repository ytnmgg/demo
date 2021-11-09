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
     * 插入
     *
     * @param userDO userDO
     */
    void insert(UserDO userDO);

    /**
     * 根据id查找
     *
     * @param id id
     * @return 结果
     */
    UserDO getById(@Param("id") String id);

    /**
     * 根据name查找
     *
     * @param name name
     * @return 结果
     */
    UserDO getByName(@Param("name") String name);

    /**
     * 获取全部用户
     *
     * @param role   role
     * @param status status
     * @param name   name
     * @return 结果
     */
    List<UserDO> list(@Param("role") String role, @Param("status") String status, @Param("name") String name);
}
