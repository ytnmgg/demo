package com.hy.project.demo.web.config;

import com.hy.project.demo.web.interceptor.HttpLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author rick.wl
 * @date 2023/04/11
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    HttpLogInterceptor httpLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpLogInterceptor);
    }
}
