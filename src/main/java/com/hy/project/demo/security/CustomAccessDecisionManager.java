package com.hy.project.demo.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
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

        for (ConfigAttribute configAttribute : configAttributes) {
            if (authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser)authentication.getPrincipal();
                Set<String> allCodes = new HashSet<>();

                if (CollectionUtils.isNotEmpty(loginUser.getPermissions())) {
                    allCodes.addAll(loginUser.getPermissions());
                }

                List<SysRole> roles = loginUser.getUser().getRoles();
                if (CollectionUtils.isNotEmpty(roles)) {
                    for (SysRole role : roles) {
                        allCodes.addAll(role.getPermissions());
                    }
                }

                if (allCodes.contains(configAttribute.getAttribute())) {
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
