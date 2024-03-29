package com.hy.project.demo.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hy.project.demo.web.util.WebUtil;
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

        if (StringUtils.isBlank(authCodes)) {
            // 没配置权限，默认按LOGIN_NORMAL处理
            if (needLoginCheck(requestUri)) {
                attributes.add((ConfigAttribute)() -> "LOGIN_NORMAL");
            }

        } else {
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

    private boolean needLoginCheck(String requestUri) {
        if (StringUtils.isBlank(requestUri)) {
            return false;
        }

        if (!requestUri.endsWith(".json")) {
            // 非ajax请求，页面请求，不用鉴权
            return false;
        }

        return !WebUtil.isEscapeUri(requestUri);
    }
}
