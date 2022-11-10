package com.hy.project.demo.service.sso.impl;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.model.sso.Account;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.service.sso.LoginService;
import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.service.sso.TokenService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.constant.CommonConstants.COOKIE_SESSION_KEY_PREFIX;
import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.USER_NAME_LENGTH_INVALID;
import static com.hy.project.demo.exception.DemoExceptionEnum.USER_PASSWORD_LENGTH_INVALID;
import static com.hy.project.demo.util.SsoUtil.createRedisUserInfoKey;
import static com.hy.project.demo.util.SsoUtil.getToken;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final long REDIS_SESSION_TIMEOUT_SECONDS = 600;
    private static final String DEFAULT_PAGE = "/index";
    private static final int DEFAULT_EXPIRE_HOUR = 1;

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RedisService redisService;

    @Autowired
    RsaService rsaService;

    @Autowired
    TokenService tokenService;

    @Override
    public String register(String name, String password) {
        // 验证用户信息
        validateUserForCreate(name, password);

        // 用RSA私钥解密前端加密后的用户登录密码
        String pwd = rsaService.decryptByPrivateKey(Base64.decodeBase64(password));
        AssertUtil.isTrue(StringUtils.isNotBlank(pwd) && pwd.length() <= MAX_PASSWORD_LENGTH,
            USER_PASSWORD_LENGTH_INVALID, "密码长度非法");

        // 加密用户密码
        String encoded = bCryptPasswordEncoder.encode(pwd);

        // 创建用户
        return userService.createNewUser(name, encoded);
    }

    @Override
    public String login(String name, String password, String callback, HttpServletResponse response)
        throws Throwable {

        // 用RSA私钥解密前端加密后的用户登录密码
        String pwd = rsaService.decryptByPrivateKey(Base64.decodeBase64(password));

        // 验证用户信息
        SysUser user = validateUserForLogin(name, pwd);

        // 生成token
        String token = tokenService.createToken(user.getUserId());

        // 缓存token
        tokenService.cacheToken(token);

        // 把用户信息存入redis（或者更新）
        userService.touchUser(user);

        // 写入cookie
        //response.addCookie(new Cookie(COOKIE_SESSION_KEY_PREFIX, token));

        return token;
    }

    @Override
    public void logout(HttpServletRequest request) {

        SysUser sysUser = userService.getMe();

        if (null != sysUser) {
            tokenService.cleanToken(sysUser.getToken());

            userService.clearUser(sysUser.getUserId());
        }
    }

    @Override
    public Account getAccountByToken(String token) {
        String key = createRedisUserInfoKey(token);
        Object accountObj = redisService.get(key);
        if (null == accountObj) {
            return null;
        }
        return JSONObject.toJavaObject((JSONObject)accountObj, Account.class);
    }

    @Override
    public void refreshToken(String token) {
        String key = createRedisUserInfoKey(token);
        redisService.expire(key, REDIS_SESSION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void validateUserForCreate(String name, String password) {
        AssertUtil.isTrue(StringUtils.isNotBlank(name) && name.length() <= MAX_NAME_LENGTH, USER_NAME_LENGTH_INVALID,
            "用户名长度非法");
    }

    private SysUser validateUserForLogin(String name, String password) {
        SysUser user = userService.loadSysUserByName(name);
        AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "用户不存在");

        // TODO：临时逻辑：超级账号第一次登陆，没有密码，用第一次登陆的密码初始化
        if (StringUtils.equals(user.getUserName(), "admin") && StringUtils.isBlank(user.getPassword())) {
            String passwordEncrypted = bCryptPasswordEncoder.encode(password);
            user.setPassword(passwordEncrypted);
            userService.updateSysUser(user);
            return user;
        }

        boolean matches = bCryptPasswordEncoder.matches(password, user.getPassword());
        AssertUtil.isTrue(matches, INVALID_PARAM_EXCEPTION, "密码不正确");

        return user;
    }
}
