package com.hy.project.demo.auth.core.service.impl;

import java.util.Date;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewUserRequest;
import com.hy.project.demo.auth.facade.model.request.LoginRequest;
import com.hy.project.demo.auth.facade.model.request.RegisterRequest;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.auth.facade.service.LoginService;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.USER_NAME_LENGTH_INVALID;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.USER_PASSWORD_LENGTH_INVALID;
import static com.hy.project.demo.common.util.DateUtil.STANDARD_STR;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Service
@DubboService
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
    RsaService rsaService;

    @Autowired
    TokenService tokenService;

    @Override
    public SimpleResult<String> register(RegisterRequest request) {
        String name = request.getName();
        String password = request.getPassword();

        // 验证用户信息
        validateUserForCreate(name, password);

        // 用RSA私钥解密前端加密后的用户登录密码
        SimpleResult<String> result = rsaService.decryptByPrivateKey(SimpleRequest.of(Base64.decodeBase64(password)));
        String pwd = result.getData();
        AssertUtil.isTrue(StringUtils.isNotBlank(pwd) && pwd.length() <= MAX_PASSWORD_LENGTH,
            USER_PASSWORD_LENGTH_INVALID, "密码长度非法");

        // 加密用户密码
        String encoded = bCryptPasswordEncoder.encode(pwd);

        // 创建用户
        CreateNewUserRequest createNewUserRequest = new CreateNewUserRequest();
        createNewUserRequest.setName(name);
        createNewUserRequest.setPassword(encoded);
        return userService.createNewUser(createNewUserRequest);
    }

    @Override
    public SimpleResult<String> login(LoginRequest request) {

        // 用RSA私钥解密前端加密后的用户登录密码
        SimpleResult<String> pwdResult = rsaService.decryptByPrivateKey(
            SimpleRequest.of(Base64.decodeBase64(request.getPassword())));

        // 验证用户信息
        SysUser user = validateUserForLogin(request.getName(), pwdResult.getData());

        // 生成token
        SimpleResult<String> tokenResult = tokenService.createToken(SimpleRequest.of(user.getUserId()));

        // 缓存token
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setToken(tokenResult.getData());
        loginInfo.setUserId(user.getUserId());
        loginInfo.setUserName(user.getUserName());
        loginInfo.setLoginIp(request.getClientIp());
        loginInfo.setLoginTime(DateUtil.format(new Date(), STANDARD_STR));
        loginInfo.setUserAgent(request.getUserAgent());
        tokenService.saveToken(SimpleRequest.of(loginInfo));

        // 把用户信息存入redis（或者更新）
        userService.touchUser(SimpleRequest.of(user));

        // 写入cookie
        //response.addCookie(new Cookie(COOKIE_SESSION_KEY_PREFIX, token));

        return SimpleResult.of(tokenResult.getData());
    }

    @Override
    public BaseResult logout(SimpleRequest<SysUser> request) {

        SysUser sysUser = request.getData();

        if (null != sysUser) {
            tokenService.removeToken(SimpleRequest.of(sysUser.getToken()));

            userService.clearUser(SimpleRequest.of(sysUser.getUserId()));
        }

        return new BaseResult();
    }

    private void validateUserForCreate(String name, String password) {
        AssertUtil.isTrue(StringUtils.isNotBlank(name) && name.length() <= MAX_NAME_LENGTH, USER_NAME_LENGTH_INVALID,
            "用户名长度非法");
    }

    private SysUser validateUserForLogin(String name, String password) {
        SimpleResult<SysUser> userResult = userService.loadSysUserByName(SimpleRequest.of(name));
        SysUser user = userResult.getData();
        AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "用户不存在");

        // TODO：临时逻辑：超级账号第一次登陆，没有密码，用第一次登陆的密码初始化
        if (StringUtils.equals(user.getUserName(), "admin") && StringUtils.isBlank(user.getPassword())) {
            //String passwordEncrypted = bCryptPasswordEncoder.encode(password);
            //user.setPassword(passwordEncrypted);
            userService.updateSysUser(SimpleRequest.of(user));
            return user;
        }

        //boolean matches = bCryptPasswordEncoder.matches(password, user.getPassword());
        //AssertUtil.isTrue(matches, INVALID_PARAM_EXCEPTION, "密码不正确");

        return user;
    }
}
