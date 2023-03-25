package com.hy.project.demo.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.web.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import static com.hy.project.demo.web.util.ResultUtil.CODE_403;
import static com.hy.project.demo.web.util.ResultUtil.buildJsonResponse;

/**
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        AccessDeniedException e) throws IOException, ServletException {
        LOGGER.info("in CustomAccessDeniedHandler");
        buildJsonResponse(httpServletResponse, AjaxResult.fail(CODE_403, "无访问权限"));
    }
}
