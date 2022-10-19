package com.hy.project.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain)
        throws ServletException, IOException {

        String profile = env.getProperty("spring.profiles.active");
        if (StringUtils.isNotBlank(profile) && StringUtils.equals(profile, "dev")) {
            HttpServletResponse response = (HttpServletResponse)httpServletResponse;
            HttpServletRequest request = (HttpServletRequest)httpServletRequest;
            String curOrigin = request.getHeader("Origin");
            response.setHeader("Access-Control-Allow-Origin", curOrigin == null ? "true" : curOrigin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            filterChain.doFilter(request, response);

        } else {

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

    }
}
