package com.hy.project.demo.auth.core.test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponseWrapper;

import com.hy.project.demo.auth.core.AuthCoreApplication;
import com.hy.project.demo.auth.facade.service.LoginService;
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
@SpringBootTest(classes = AuthCoreApplication.class)
@TestPropertySource("classpath:test.properties")
public class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Test
    public void registerTest() {
        //loginService.register("rick", "123");
    }

    @Test
    public void loginTest() throws Throwable {
        //loginService.login("rick", "123", null, null, new ResponseWrapper());
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
