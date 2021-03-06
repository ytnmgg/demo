package com.hy.project.demo.service.sso.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.model.DemoResult;
import com.hy.project.demo.model.sso.Account;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.service.sso.LoginService;
import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.hy.project.demo.constant.CommonConstants.COOKIE_SESSION_KEY_PREFIX;
import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.USER_NAME_LENGTH_INVALID;
import static com.hy.project.demo.exception.DemoExceptionEnum.USER_PASSWORD_LENGTH_INVALID;
import static com.hy.project.demo.util.SsoUtil.getToken;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final String REDIS_SESSION_KEY_PREFIX = "SESSION";
    private static final long REDIS_SESSION_TIMEOUT_SECONDS = 600;
    private static final String DEFAULT_PAGE = "/index";

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RedisService redisService;

    @Autowired
    RsaService rsaService;

    @Override
    public void register(String name, String password) {
        // ??????????????????
        validateUserForCreate(name, password);

        // ??????????????????
        String encoded = bCryptPasswordEncoder.encode(password);

        // ????????????
        userService.createNewUser(name, encoded, null);
    }

    @Override
    public DemoResult<Void> login(String name, String password, String callback, HttpServletResponse response)
        throws Throwable {

        // ???RSA????????????????????????????????????????????????
        String pwd = rsaService.decryptByPrivateKey(Base64.decodeBase64(password));

        // ??????????????????
        User user = validateUserForLogin(name, pwd);

        // ??????token
        String nameEncoded = DigestUtils.md5DigestAsHex(name.getBytes());
        String token = String.format("%s_%s", nameEncoded, UUID.randomUUID());

        // ?????????????????????redis
        String key = createRedisSessionKey(token);
        Account account = user.getAccount();
        redisService.set(key, account, REDIS_SESSION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // ??????cookie
        response.addCookie(new Cookie(COOKIE_SESSION_KEY_PREFIX, token));

        // ????????????
        return DemoResult.buildSuccessResult(null);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = getToken(request, COOKIE_SESSION_KEY_PREFIX);
        if (StringUtils.isBlank(token)) {
            return;
        }

        String key = createRedisSessionKey(token);
        redisService.remove(key);
    }

    @Override
    public Account getAccountByToken(String token) {
        String key = createRedisSessionKey(token);
        Object accountObj = redisService.get(key);
        if (null == accountObj) {
            return null;
        }
        return JSONObject.toJavaObject((JSONObject)accountObj, Account.class);
    }

    @Override
    public void refreshToken(String token) {
        String key = createRedisSessionKey(token);
        redisService.expire(key, REDIS_SESSION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private String createRedisSessionKey(String token) {
        return String.format("%s_%s", REDIS_SESSION_KEY_PREFIX, token);
    }

    private void validateUserForCreate(String name, String password) {
        AssertUtil.isTrue(StringUtils.isNotBlank(name) && name.length() <= MAX_NAME_LENGTH, USER_NAME_LENGTH_INVALID,
            "?????????????????????");
        AssertUtil.isTrue(StringUtils.isNotBlank(password) && password.length() <= MAX_PASSWORD_LENGTH,
            USER_PASSWORD_LENGTH_INVALID,
            "????????????????????????");
    }

    private User validateUserForLogin(String name, String password) {
        User user = userService.getUserByName(name);
        AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "???????????????????????????...");

        boolean matches = bCryptPasswordEncoder.matches(password, user.getPassword());
        AssertUtil.isTrue(matches, INVALID_PARAM_EXCEPTION, "???????????????????????????...");

        return user;
    }
}
