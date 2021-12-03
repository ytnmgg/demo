package com.hy.project.demo;

import java.util.List;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.service.user.UserService;
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
@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource("classpath:test.properties")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    public void listUsersTest() {
        DemoResult<PageResult<List<User>>> result = userService.pageListUsers(1, 10);
        Assert.notNull(result);
    }
}
