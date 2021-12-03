package com.hy.project.demo.service.user.impl;

import java.util.List;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.repository.UserRepository;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
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
    public User getUserByName(String name) {
        return userRepository.getByName(name);
    }

    @Override
    public void createNewUser(String name, String password, String role) {

        AssertUtil.notBlank(name, INVALID_PARAM_EXCEPTION, "用户名不能为空");
        AssertUtil.notBlank(password, INVALID_PARAM_EXCEPTION, "密码不能为空");

        String r = role;
        if (StringUtils.isBlank(r)) {
            r = "MEMBER";
        }

        User existed = userRepository.getByName(name);
        AssertUtil.isNull(existed, INVALID_PARAM_EXCEPTION, "用户已经存在");

        User user = new User();
        user.setPassword(password);
        user.setStatus("ACTIVE");
        user.setName(name);
        user.setRole(r);
        userRepository.insert(user);
    }

    @Override
    public DemoResult<PageResult<List<User>>> pageListUsers(int pageIndex, int pageSize) {
        PageResult<List<User>> result = userRepository.pageList(pageIndex, pageSize);
        return DemoResult.buildSuccessResult(result);
    }
}
