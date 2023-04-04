package com.hy.project.demo.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import static com.hy.project.demo.web.constant.WebConstants.LOGIN_PAGE_URL;
import static com.hy.project.demo.web.constant.WebConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.web.constant.WebConstants.LOGOUT_REQUEST_URL;

/**
 * @author rick.wl
 * @date 2023/03/24
 */
public class WebUtil {
    private final static List<String> ESCAPE_PATH = new ArrayList<>();

    static {
        ESCAPE_PATH.add(LOGIN_PAGE_URL);
        ESCAPE_PATH.add(LOGIN_REQUEST_URL);
        ESCAPE_PATH.add(LOGOUT_REQUEST_URL);
        ESCAPE_PATH.add("/get_encrypt_key.json");
        ESCAPE_PATH.add("/manifest.json");
        ESCAPE_PATH.add("/user/register.json");

        // grafana内部调用，单独的鉴权逻辑
        ESCAPE_PATH.add("/grafana-auth/token.json");
    }

    public static boolean isEscapeUri(String uri) {
        return ESCAPE_PATH.contains(uri);
    }

    public static String getTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "ACCESS_TOKEN");
        return null != cookie ? cookie.getValue() : null;
    }


    public static String getTokenFromHeader(HttpServletRequest request, String tokenHeader) {
        String token = request.getHeader(tokenHeader);
        return token;
    }
}
