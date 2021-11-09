package com.hy.project.demo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

/**
 * @author rick.wl
 * @date 2021/11/09
 */
public class SsoUtil {

    public static String getToken(HttpServletRequest request, String key) {
        Cookie cookie = WebUtils.getCookie(request, key);
        return null != cookie ? cookie.getValue() : null;
    }
}
