package com.hy.project.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.service.DynamicConfigService;
import com.hy.project.demo.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author rick.wl
 * @date 2021/08/16
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "AuthFilter")
@Order(Ordered.LOWEST_PRECEDENCE)
public class AuthFilter extends OncePerRequestFilter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    DynamicConfigService dynamicConfigService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String ip = dynamicConfigService.get("valid_ip");
        if (StringUtils.isNotBlank(ip)) {
            String remoteIp = IpUtil.getIpAddr(request);
            if (!StringUtils.equals(ip, remoteIp)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                LOGGER.warn("invalid ip, reject it: {}", remoteIp);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
