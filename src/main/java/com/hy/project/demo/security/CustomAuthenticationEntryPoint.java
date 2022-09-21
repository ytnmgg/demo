package com.hy.project.demo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.model.AjaxResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static com.hy.project.demo.util.ResultUtil.CODE_401;
import static com.hy.project.demo.util.ResultUtil.buildJsonResponse;

/**
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        AuthenticationException e) throws IOException, ServletException {

        buildJsonResponse(httpServletResponse, AjaxResult.fail(CODE_401, "请先登录"));
    }
}
