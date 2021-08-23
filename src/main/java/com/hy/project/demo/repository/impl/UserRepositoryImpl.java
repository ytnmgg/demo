package com.hy.project.demo.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hy.project.demo.model.User;
import com.hy.project.demo.mybatis.entity.UserDO;
import com.hy.project.demo.mybatis.mapper.UserMapper;
import com.hy.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> list(String type, String name) {

        List<UserDO> dos = userMapper.list(type, name);

        if (CollectionUtils.isEmpty(dos)) {
            return new ArrayList<>();
        }

        return dos.stream().map(this::toModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private User toModel(UserDO userDO) {
        if (null == userDO) {
            return null;
        }
        User user = new User();
        user.setUid(userDO.getUid());
        user.setGmtCreate(userDO.getGmtCreate());
        user.setGmtModified(userDO.getGmtModified());
        user.setType(userDO.getType());
        user.setName(userDO.getName());

        return user;
    }
}
