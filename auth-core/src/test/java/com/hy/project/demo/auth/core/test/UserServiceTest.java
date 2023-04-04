package com.hy.project.demo.auth.core.test;

import java.util.List;

import com.hy.project.demo.auth.core.AuthCoreApplication;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthCoreApplication.class)
@TestPropertySource("classpath:test.properties")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    public void listUsersTest() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageIndex(1);
        pageRequest.setPageSize(10);
        PageResult<List<SysUser>> result = userService.pageListUsers(pageRequest);
        Assert.notNull(result);
    }
}
