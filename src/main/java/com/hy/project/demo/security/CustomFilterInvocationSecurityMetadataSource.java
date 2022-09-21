package com.hy.project.demo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/09/13
 */
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取请求路径
        String requestUri = ((FilterInvocation)object).getRequest().getRequestURI();

        // TODO 加载配置：requestUrl -> 权限配置

        // TODO 根据 requestUrl 从上面的配置中获取权限配置信息

        // mock
        List<ConfigAttribute> attributes = new ArrayList<>();
        if ("/user/list.json".equals(requestUri)) {
            attributes.add(new ConfigAttribute() {
                @Override
                public String getAttribute() {
                    return "USER_QUERY";
                }
            });
        }
        return attributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
