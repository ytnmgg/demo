package com.hy.project.demo.web.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.auth.facade.service.RoleService;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.web.util.WebUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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

    @DubboReference
    TokenService tokenService;

    @DubboReference
    RoleService roleService;

    @Value("${sso.token.header}")
    private String tokenHeader;

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

        String method = httpServletRequest.getMethod();
        if (StringUtils.equals(method, HttpMethod.OPTIONS.name())) {
            LOGGER.info("ignore options request...");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        LOGGER.info("in JwtAuthenticationTokenFilter");
        if (needHandle(httpServletRequest)) {
            buildAuthContext(httpServletRequest);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void buildAuthContext(HttpServletRequest httpServletRequest) {
        // 获取请求携带的令牌
        //String token = WebUtil.getTokenFromHeader(httpServletRequest, tokenHeader);
        //
        //if (StringUtils.isBlank(token)) {
        //    token = WebUtil.getTokenFromCookie(httpServletRequest);
        //}
        String token = WebUtil.getTokenFromCookie(httpServletRequest);

        RpcResult<SysUser> getUserResult = tokenService.getUserByToken(RpcRequest.of(token));
        if (null == getUserResult) {
            return;
        }

        SysUser user = getUserResult.getData();

        // 设置安全上下文，业务代码获取当前用户信息都从SecurityContextHolder.getContext()获取
        if (null != user) {

            RpcResult<List<String>> permissionResult = roleService.getPermissions(RpcRequest.of(user.getRoles()));
            if (null == permissionResult || CollectionUtils.isEmpty(permissionResult.getData())) {
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user,
                null, buildAuthorities(permissionResult.getData()));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            // 设置到上下文，其它地方直接用
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(List<String> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String permission : permissions) {
            grantedAuthorities.add((GrantedAuthority)() -> permission);
        }
        return grantedAuthorities;
    }

    private boolean needHandle(HttpServletRequest request) {
        String method = request.getMethod();
        return HANDLE_METHODS.contains(method);
    }
}
