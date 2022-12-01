package com.hy.project.demo.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.model.RequestContext;
import com.hy.project.demo.model.RequestContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author rick.wl
 * @date 2021/08/16
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "RequestContextInitialFilter")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestContextInitialFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain)
        throws ServletException, IOException {

        RequestContext context = RequestContextHolder.getCurrentRequestContext();
        context.setEnterTime(System.currentTimeMillis());
        context.setRequest(httpServletRequest);

        String requestId = httpServletRequest.getHeader("request-id");
        if (StringUtils.isEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        context.setRequestId(requestId);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
