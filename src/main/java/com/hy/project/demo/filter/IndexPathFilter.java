package com.hy.project.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author rick.wl
 * @date 2021/09/09
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "IndexPathFilter")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class IndexPathFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain) throws ServletException, IOException {

        String path = httpServletRequest.getRequestURI();

        if (redirectToIndex(path)) {
            httpServletRequest.getRequestDispatcher("/").forward(httpServletRequest, httpServletResponse);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private boolean redirectToIndex(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm") || path.endsWith(".vm")) {
            return true;
        }

        String b = path.substring(path.lastIndexOf("/") + 1);
        return !b.contains(".");
    }
}
