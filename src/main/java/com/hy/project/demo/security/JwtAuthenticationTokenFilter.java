package com.hy.project.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.service.auth.RoleService;
import com.hy.project.demo.service.auth.RsaService;
import com.hy.project.demo.service.auth.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    private static final List<String> HANDLE_METHODS = new ArrayList<>();

    @Autowired
    RsaService rsaService;

    @Autowired
    TokenService tokenService;

    @Autowired
    RoleService roleService;

    static {
        HANDLE_METHODS.add(HttpMethod.GET.name());
        HANDLE_METHODS.add(HttpMethod.HEAD.name());
        HANDLE_METHODS.add(HttpMethod.POST.name());
        HANDLE_METHODS.add(HttpMethod.PUT.name());
        HANDLE_METHODS.add(HttpMethod.DELETE.name());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("in JwtAuthenticationTokenFilter");
        if (needHandle(httpServletRequest)) {

            SysUser user = tokenService.getUserByToken(httpServletRequest);

            // 设置安全上下文，业务代码获取当前用户信息都从SecurityContextHolder.getContext()获取
            if (null != user) {
                LoginUser loginUser = roleService.buildLoginUser(user);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginUser,
                    null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean needHandle(HttpServletRequest request) {
        String method = request.getMethod();
        return HANDLE_METHODS.contains(method);
    }
}
