package com.hy.project.demo.service.user.impl;

import java.util.List;

import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.repository.UserRepository;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User getUserById(String uid) {
        return userRepository.getById(uid);
    }

    @Override
    public SysUser loadSysUserByName(String name) {
        SysUser sysUser = userRepository.findByName(name);
        // TODO 补充role信息

        return sysUser;
    }

    @Override
    public SysUser loadSysUserByUserId(String userId) {
        SysUser sysUser = userRepository.findByUserId(userId);
        // TODO 补充role信息

        return sysUser;
    }

    @Override
    public String createNewUser(String name, String password) {
        SysUser sysUser = new SysUser();
        sysUser.setUserType("00");
        sysUser.setNickName("nick");
        sysUser.setUserName(name);
        sysUser.setPassword(password);

        // TODO, 干掉，用uk行锁解决
        SysUser existed = userRepository.findByName(name);
        AssertUtil.isNull(existed, INVALID_PARAM_EXCEPTION, "用户已经存在: %s", name);

        return userRepository.insert(sysUser);
    }

    @Override
    public PageResult<List<SysUser>> pageListUsers(int pageIndex, int pageSize) {
        return userRepository.pageList(pageIndex, pageSize);
    }
}
