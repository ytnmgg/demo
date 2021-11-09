package com.hy.project.demo.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.sso.Account;
import com.hy.project.demo.model.sso.AccountContext;
import com.hy.project.demo.service.sso.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.hy.project.demo.constant.CommonConstants.COOKIE_SESSION_KEY_PREFIX;
import static com.hy.project.demo.constant.CommonConstants.LOGIN_PAGE_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGOUT_REQUEST_URL;
import static com.hy.project.demo.exception.DemoExceptionEnum.AUTH_STATUS_INVALID;
import static com.hy.project.demo.util.SsoUtil.getToken;

/**
 * @author rick.wl
 * @date 2021/08/16
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "AuthFilter")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuthFilter extends OncePerRequestFilter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    private final static String TICKET_NAME = "login_ticket";

    private final static String SESSION_KEY_PREFIX = "SESSION:";
    private final static Set<String> ESCAPE_PATH = new HashSet<>();

    static {
        ESCAPE_PATH.add(LOGIN_PAGE_URL);
        ESCAPE_PATH.add(LOGIN_REQUEST_URL);
        ESCAPE_PATH.add(LOGOUT_REQUEST_URL);
        ESCAPE_PATH.add("/get_encrypt_key.json");
        ESCAPE_PATH.add("/manifest.json");
    }

    @Autowired
    LoginService loginService;

    @Autowired
    Environment environment;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (needAuth(request.getRequestURI())) {
            // 从cookie中获取token
            String token = getToken(request, COOKIE_SESSION_KEY_PREFIX);

            if (StringUtils.isBlank(token)) {
                redirectOrResponse(request, response);
                return;
            }

            // 根据ticket从redis中获取账号信息
            Account account = loginService.getAccountByToken(token);
            if (null == account) {
                redirectOrResponse(request, response);
                return;
            }

            // 存入jvm缓存中后续使用
            AccountContext.setAccount(account);

            // 刷新token过期时间
            loginService.refreshToken(token);
        }

        filterChain.doFilter(request, response);
    }

    private boolean needAuth(String path) {
        String authClose = environment.getProperty("auth.close");
        if (StringUtils.equalsIgnoreCase(authClose, Boolean.TRUE.toString())) {
            return false;
        }

        if (ESCAPE_PATH.contains(path)) {
            return false;
        } else if (StringUtils.endsWithIgnoreCase(path, "js") || StringUtils.endsWithIgnoreCase(path, "css")
            || StringUtils.endsWithIgnoreCase(path, "ico") || StringUtils.endsWithIgnoreCase(path, "png")) {
            return false;
        } else {
            return true;
        }
    }

    private void redirectOrResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();

        if (uri.endsWith(".json")) {
            buildAuthFailResponse(response, request.getRequestURL().toString());
        } else {
            String redirectUrl = LOGIN_PAGE_URL + "?callback=" + request.getRequestURL().toString();
            response.sendRedirect(redirectUrl);
        }
    }

    private void buildAuthFailResponse(HttpServletResponse response, String callbackUrl) {
        response.setStatus(HttpStatus.SC_OK);

        PrintWriter writer = null;
        try {
            response.setCharacterEncoding("utf-8");
            writer = response.getWriter();

            DemoResult<String> result = new DemoResult<>();
            result.setMessage(AUTH_STATUS_INVALID.getDescription());
            result.setCode(AUTH_STATUS_INVALID.getCode());
            result.setData(callbackUrl);

            writer.write(JSON.toJSONString(result));
            response.addHeader("content-type", "content-type: application/json;charset=UTF-8");

        } catch (Throwable e) {
            logger.error("build rule hit response failed", e);
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
