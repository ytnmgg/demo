package com.hy.project.demo.web.security;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/09/13
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
        throws AccessDeniedException, InsufficientAuthenticationException {

        if (CollectionUtils.isEmpty(configAttributes)) {
            // 未配置权限信息，认为通过
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (CollectionUtils.isEmpty(authorities)) {
            throw new AccessDeniedException("无访问权限");
        }

        for (ConfigAttribute configAttribute : configAttributes) {

            for (GrantedAuthority authority : authorities) {
                if (StringUtils.equals(authority.getAuthority(), configAttribute.getAttribute())) {
                    // 权限或者角色有满足的，放行
                    return;
                }
            }
        }

        throw new AccessDeniedException("无访问权限");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
