package com.hy.project.demo.auth.core.mybatis.mapper;

import java.util.List;

import com.hy.project.demo.auth.core.mybatis.entity.UserRoleRelationDO;
import com.hy.project.demo.common.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Mapper
public interface UserRoleRelationMapper extends CrudMapper<UserRoleRelationDO, String> {

    List<UserRoleRelationDO> findByUserId(String userId);

    void deleteByUserId(String userId);

    List<UserRoleRelationDO> findByUserIds(List<String> userIds);

}
