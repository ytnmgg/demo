package com.hy.project.demo.auth.core.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hy.project.demo.auth.core.model.SequenceNameEnum;
import com.hy.project.demo.auth.core.mybatis.entity.UserDO;
import com.hy.project.demo.auth.core.mybatis.mapper.UserMapper;
import com.hy.project.demo.auth.core.repository.UserRepository;
import com.hy.project.demo.auth.core.util.IdGenerator;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import static com.hy.project.demo.common.util.DateUtil.STANDARD_STR;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    UserMapper userMapper;

    @Autowired
    IdGenerator idGenerator;

    @Override
    public String insert(SysUser sysUser) {
        UserDO userDO = toDo(sysUser);
        String userId = idGenerator.generateId(SequenceNameEnum.SEQ_USER_ID);
        userDO.setUserId(userId);
        userMapper.insert(userDO);
        return userId;
    }

    @Override
    public SysUser findByName(String name) {
        UserDO userDO = userMapper.findByName(name);
        return null == userDO ? null : toModel(userDO);
    }

    @Override
    public SysUser findByUserId(String userId) {
        UserDO userDO = userMapper.findById(userId);
        return null == userDO ? null : toModel(userDO);
    }

    @Override
    public void updateUser(SysUser sysUser) {
        UserDO userDO = toDo(sysUser);
        userMapper.update(userDO);
    }

    @Override
    public SysUser lockByUserId(String userId) {
        UserDO userDO = userMapper.lockById(userId);
        return null == userDO ? null : toModel(userDO);
    }

    @Override
    public PageResult<List<SysUser>> pageList(int pageIndex, int pageSize) {
        PageResult<List<SysUser>> result = new PageResult<>();
        result.setData(new ArrayList<>());
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);

        long total = userMapper.count();
        result.setTotalCount(total);

        if (total == 0) {
            return result;
        }

        int offset = (pageIndex - 1) * pageSize;
        List<UserDO> userDOs = userMapper.findByPage(offset, pageSize);

        if (CollectionUtils.isEmpty(userDOs)) {
            return result;
        }

        result.setData(userDOs.stream().map(this::toModel).filter(Objects::nonNull).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void deleteByUserId(String userId) {
        userMapper.deleteById(userId);
    }

    private UserDO toDo(SysUser sysUser) {
        if (null == sysUser) {
            return null;
        }

        UserDO userDO = new UserDO();
        userDO.setUserId(sysUser.getUserId());
        userDO.setUserName(sysUser.getUserName());
        userDO.setNickName(sysUser.getNickName());
        userDO.setUserType(sysUser.getUserType());
        userDO.setEmail(sysUser.getEmail());
        userDO.setPhone(sysUser.getPhonenumber());
        userDO.setSex(sysUser.getSex());
        userDO.setAvatar(sysUser.getAvatar());
        userDO.setPassword(sysUser.getPassword());
        userDO.setStatus(sysUser.getStatus());
        userDO.setDelFlag(sysUser.getDelFlag());
        userDO.setLoginIp(sysUser.getLoginIp());
        userDO.setLoginDate(sysUser.getLoginDate());
        userDO.setCreateBy(sysUser.getUserName());
        userDO.setCreateTime(DateUtil.parse(sysUser.getCreateTime(), STANDARD_STR));
        userDO.setUpdateBy(sysUser.getUserName());
        userDO.setUpdateTime(new Date());
        userDO.setRemark(null);
        return userDO;
    }

    private SysUser toModel(UserDO userDO) {
        if (null == userDO) {
            return null;
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userDO.getUserId());
        sysUser.setUserType(userDO.getUserType());
        sysUser.setNickName(userDO.getNickName());
        sysUser.setUserName(userDO.getUserName());
        sysUser.setEmail(userDO.getEmail());
        sysUser.setPhonenumber(userDO.getPhone());
        sysUser.setSex(userDO.getSex());
        sysUser.setAvatar(userDO.getAvatar());
        sysUser.setPassword(userDO.getPassword());
        sysUser.setStatus(userDO.getStatus());
        sysUser.setDelFlag(userDO.getDelFlag());
        sysUser.setLoginIp(userDO.getLoginIp());
        sysUser.setLoginDate(userDO.getLoginDate());
        if (null != userDO.getCreateTime()) {
            sysUser.setCreateTime(DateUtil.format(userDO.getCreateTime(), STANDARD_STR));
        }
        if (null != userDO.getUpdateTime()) {
            sysUser.setUpdateTime(DateUtil.format(userDO.getUpdateTime(), STANDARD_STR));
        }
        return sysUser;
    }
}
