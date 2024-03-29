package com.hy.project.demo.web.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.hy.project.demo.web.constant.WebConstants.LOGIN_PAGE_URL;

/**
 * @author rick.wl
 * @date 2021/09/09
 */
//@Component
//@WebFilter(urlPatterns = "/*", filterName = "IndexPathFilter")
//@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class IndexPathFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexPathFilter.class);

    private final static Set<String> ESCAPE_PATH = new HashSet<>();

    static {
        ESCAPE_PATH.add(LOGIN_PAGE_URL);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("in IndexPathFilter");
        String path = httpServletRequest.getRequestURI();

        if (redirectToIndex(path)) {
            httpServletRequest.getRequestDispatcher("/").forward(httpServletRequest, httpServletResponse);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private boolean redirectToIndex(String path) {
        if (ESCAPE_PATH.contains(path)) {
            return false;
        } else if (path.startsWith("/actuator")) {
            return false;
        } else if (path.endsWith(".html") || path.endsWith(".htm") || path.endsWith(".vm")) {
            return true;
        } else {
            String b = path.substring(path.lastIndexOf("/") + 1);
            return !b.contains(".");
        }
    }
}
