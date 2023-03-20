package com.hy.project.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Ø *
 *
 * @author rick.wl
 * @date 2021/08/16
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "CorsFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
    private static final String HEADER_ORIGIN = "Origin";
    private static final String HEADER_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String HEADER_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String HEADER_REQUEST_HEADERS = "Access-Control-Request-Headers";
    @Autowired
    private Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        LOGGER.info("in CorsFilter");

        try {
            String origin = request.getHeader(HEADER_ORIGIN);

            // TODO @RICK 先全部允许，后面走配置
            if (StringUtils.isNotBlank(origin)) {
                response.setHeader(HEADER_ALLOW_ORIGIN, request.getHeader(HEADER_ORIGIN));
                response.setHeader(HEADER_ALLOW_CREDENTIALS, "true");
                response.setHeader(HEADER_ALLOW_HEADERS, request.getHeader(HEADER_REQUEST_HEADERS));
                response.setHeader(HEADER_ALLOW_METHODS, "GET,POST");
            }

        } catch (Throwable e) {
            // CORS处理异常不影响后续业务，只记录日志
            LOGGER.error("cors filter error", e);
        }

        filterChain.doFilter(request, response);
    }
}
