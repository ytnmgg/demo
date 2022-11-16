package com.hy.project.demo.util;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.util.WebUtils;

import static com.hy.project.demo.constant.CommonConstants.LOGIN_PAGE_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGIN_REQUEST_URL;
import static com.hy.project.demo.constant.CommonConstants.LOGOUT_REQUEST_URL;
import static com.hy.project.demo.constant.RedisConstants.KEY_TOKEN_PREFIX;
import static com.hy.project.demo.constant.RedisConstants.KEY_USER_INFO_PREFIX;

/**
 * @author rick.wl
 * @date 2021/11/09
 */
public class SsoUtil {

    public static final String LOGIN_USER_KEY = "login_user_key";
    public static final String LOGIN_USER_UID = "login_user_uid";

    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    private final static List<String> ESCAPE_PATH = new ArrayList<>();

    static {
        ESCAPE_PATH.add(LOGIN_PAGE_URL);
        ESCAPE_PATH.add(LOGIN_REQUEST_URL);
        ESCAPE_PATH.add(LOGOUT_REQUEST_URL);
        ESCAPE_PATH.add("/get_encrypt_key.json");
        ESCAPE_PATH.add("/manifest.json");
        ESCAPE_PATH.add("/user/register.json");
    }

    public static boolean isEscapeUri(String uri) {
        return ESCAPE_PATH.contains(uri);
    }

    public static String createToken(String uuid, String uid, Key secret) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(LOGIN_USER_KEY, uuid);
        claims.put(LOGIN_USER_UID, uid);

        String token = Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, secret).compact();
        return token;
    }

    public static Claims parseToken(String token, Key secret) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    public static String getUuid(Claims claims) {
        return (String)claims.get(LOGIN_USER_KEY);
    }

    public static String getUid(Claims claims) {
        return (String)claims.get(LOGIN_USER_UID);
    }

    public static String getTokenFromHeader(HttpServletRequest request, String tokenHeader) {
        String token = request.getHeader(tokenHeader);
        return token;
    }

    private String getTokenKey(String uuid) {
        return LOGIN_TOKEN_KEY + uuid;
    }

    public static String getToken(HttpServletRequest request, String key) {
        Cookie cookie = WebUtils.getCookie(request, key);
        return null != cookie ? cookie.getValue() : null;
    }

    public static String createRedisUserInfoKey(String userId) {
        return String.format("%s_%s", KEY_USER_INFO_PREFIX, userId);
    }

    public static String createRedisTokenKey(String token) {
        return String.format("%s_%s", KEY_TOKEN_PREFIX, token);
    }
}
