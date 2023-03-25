package com.hy.project.demo.web.filter;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.web.model.RequestContext;
import com.hy.project.demo.web.model.RequestContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestContextInitialFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain)
        throws ServletException, IOException {
        LOGGER.info("in RequestContextInitialFilter");
        RequestContext context = RequestContextHolder.getCurrentRequestContext();
        context.setEnterTime(System.currentTimeMillis());
        context.setRequest(httpServletRequest);

        String requestId = httpServletRequest.getHeader("request-id");
        if (StringUtils.isEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        context.setRequestId(requestId);

        initMdc(httpServletRequest, requestId);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void initMdc(HttpServletRequest request, String requestId) {
        MDC.put("requestId", requestId);
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("host", request.getHeader("Host"));

        MDC.put("realIp", request.getHeader("x-real-ip"));
        MDC.put("forwardedIp", request.getHeader("x-forwarded-for"));
        MDC.put("hostAddress", request.getHeader("x-host-address"));

        Date now = new Date();
        MDC.put("msec", String.format("%.3f", now.getTime() / 1000.0));
    }
}
