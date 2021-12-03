package com.hy.project.demo.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.mybatis.entity.UserDO;
import com.hy.project.demo.mybatis.mapper.UserMapper;
import com.hy.project.demo.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
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
    public void insert(User user) {
        userMapper.insert(toDo(user));
    }

    @Override
    public User getById(String uid) {
        return toModel(userMapper.getById(uid));
    }

    @Override
    public User getByName(String name) {
        return toModel(userMapper.getByName(name));
    }

    @Override
    public List<User> list(String role, String status, String name) {
        List<UserDO> dos = userMapper.list(role, status, name);

        if (CollectionUtils.isEmpty(dos)) {
            return new ArrayList<>();
        }

        return dos.stream().map(this::toModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public PageResult<List<User>> pageList(int pageIndex, int pageSize) {
        PageResult<List<User>> result = new PageResult<>();
        result.setData(new ArrayList<>());
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);

        long total = userMapper.countForPageList();
        result.setTotalCount(total);

        if (total == 0) {
            return result;
        }

        int offset = (pageIndex - 1) * pageSize;
        List<UserDO> userDOs = userMapper.pageList(offset, pageSize);

        if (CollectionUtils.isEmpty(userDOs)) {
            return result;
        }

        result.setData(userDOs.stream().map(this::toModel).filter(Objects::nonNull).collect(Collectors.toList()));
        return result;
    }

    private UserDO toDo(User user) {
        if (null == user) {
            return null;
        }

        UserDO userDO = new UserDO();
        if (StringUtils.isNotBlank(user.getUid())) {
            userDO.setId(Long.valueOf(user.getUid()));
        }
        if (null != user.getGmtCreate()) {
            userDO.setGmtCreate(user.getGmtCreate());
        }
        if (null != user.getGmtModified()) {
            userDO.setGmtModified(user.getGmtModified());
        }

        userDO.setName(user.getName());
        userDO.setRole(user.getRole());
        userDO.setPassword(user.getPassword());
        userDO.setStatus(user.getStatus());
        return userDO;
    }

    private User toModel(UserDO userDO) {
        if (null == userDO) {
            return null;
        }
        User user = new User();
        user.setGmtCreate(userDO.getGmtCreate());
        user.setGmtModified(userDO.getGmtModified());
        user.setPassword(userDO.getPassword());
        user.setStatus(userDO.getStatus());
        user.setUid(String.valueOf(userDO.getId()));
        user.setName(userDO.getName());
        user.setRole(userDO.getRole());
        return user;
    }
}
