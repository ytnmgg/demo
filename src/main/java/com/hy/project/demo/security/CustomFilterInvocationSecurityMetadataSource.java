package com.hy.project.demo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    AuthorityConfigManager authorityConfigManager;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取请求路径
        String requestUri = ((FilterInvocation)object).getRequest().getRequestURI();

        String authCodes = authorityConfigManager.getAuthorities(requestUri);

        List<ConfigAttribute> attributes = new ArrayList<>();

        if (StringUtils.isNotBlank(authCodes)) {
            for (String code : StringUtils.split(authCodes, ",")) {
                attributes.add((ConfigAttribute)() -> code);
            }
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
