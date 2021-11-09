package com.hy.project.demo;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponseWrapper;

import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.service.sso.LoginService;
import org.apache.catalina.connector.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource("classpath:test.properties")
public class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Test
    public void registerTest() {
        loginService.register("rick", "123");
    }

    @Test
    public void loginTest() throws Throwable {
        loginService.login("rick", "123",null, new ResponseWrapper());
    }

    @Test
    public void refreshTest() {
        loginService.refreshToken("891f490e5d7bdb06d90d56f8d7db405f_0d4b3bcf-002c-4a38-bf33-fb3ef2a6dc62");
    }

    private static class ResponseWrapper extends HttpServletResponseWrapper {
        public ResponseWrapper() {
            super(new Response());
        }

        @Override
        public void addCookie(Cookie cookie) {
        }
    }
}
