package com.hy.project.demo.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.web.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static com.hy.project.demo.web.util.ResultUtil.CODE_401;
import static com.hy.project.demo.web.util.ResultUtil.buildJsonResponse;

/**
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        AuthenticationException e) throws IOException, ServletException {
        LOGGER.info("in CustomAuthenticationEntryPoint");
        buildJsonResponse(httpServletResponse, AjaxResult.fail(CODE_401, "请先登录"));
    }
}
