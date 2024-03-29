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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author rick.wl
 * @date 2021/08/16
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "RequestContextInitialFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestContextInitialFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestContextInitialFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain)
        throws ServletException, IOException {

        String method = httpServletRequest.getMethod();
        if (StringUtils.equals(method, HttpMethod.OPTIONS.name())) {
            LOGGER.info("ignore options request...");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String traceId = httpServletRequest.getHeader("request-id");
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            LOGGER.info("create traceId in RequestContextInitialFilter: {}", traceId);
        }

        initMdc(httpServletRequest, traceId);

        LOGGER.info("in RequestContextInitialFilter");
        RequestContext context = RequestContextHolder.getCurrentRequestContext();
        context.setEnterTime(System.currentTimeMillis());
        context.setRequest(httpServletRequest);
        context.setTraceId(traceId);

        httpServletResponse.setHeader("request-id", traceId);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void initMdc(HttpServletRequest request, String traceId) {
        MDC.put("traceId", traceId);
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("host", request.getHeader("Host"));

        MDC.put("realIp", request.getHeader("x-real-ip"));
        MDC.put("forwardedIp", request.getHeader("x-forwarded-for"));
        MDC.put("hostAddress", request.getHeader("x-host-address"));

        Date now = new Date();
        MDC.put("msec", String.format("%.3f", now.getTime() / 1000.0));
    }
}
