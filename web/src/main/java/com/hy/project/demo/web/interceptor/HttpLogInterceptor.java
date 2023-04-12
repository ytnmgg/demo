package com.hy.project.demo.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author rick.wl
 * @date 2023/04/11
 */
@Component
public class HttpLogInterceptor implements HandlerInterceptor {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpLogInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        @Nullable ModelAndView modelAndView) throws Exception {

        LOGGER.info("in HttpLogInterceptor, we already have HttpLogAdvice, so this is just for test.");

    }
}
