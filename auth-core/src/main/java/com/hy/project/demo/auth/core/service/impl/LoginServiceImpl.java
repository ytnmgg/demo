package com.hy.project.demo.auth.core.service.impl;

import java.util.Date;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewUserRequest;
import com.hy.project.demo.auth.facade.model.request.LoginRequest;
import com.hy.project.demo.auth.facade.model.request.RegisterRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.auth.facade.service.LoginService;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.auth.facade.service.UserService;
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
    public RpcResult<String> register(RpcRequest<RegisterRequest> request) {
        String name = request.getData().getName();
        String password = request.getData().getPassword();

        // 验证用户信息
        validateUserForCreate(name, password);

        // 用RSA私钥解密前端加密后的用户登录密码
        RpcResult<String> result = rsaService.decryptByPrivateKey(RpcRequest.of(Base64.decodeBase64(password)));
        String pwd = result.getData();
        AssertUtil.isTrue(StringUtils.isNotBlank(pwd) && pwd.length() <= MAX_PASSWORD_LENGTH,
            USER_PASSWORD_LENGTH_INVALID, "密码长度非法");

        // 加密用户密码
        String encoded = bCryptPasswordEncoder.encode(pwd);

        // 创建用户
        CreateNewUserRequest createNewUserRequest = new CreateNewUserRequest();
        createNewUserRequest.setName(name);
        createNewUserRequest.setPassword(encoded);
        return userService.createNewUser(RpcRequest.of(createNewUserRequest));
    }

    @Override
    public RpcResult<String> login(RpcRequest<LoginRequest> request) {

        // 用RSA私钥解密前端加密后的用户登录密码
        RpcResult<String> pwdResult = rsaService.decryptByPrivateKey(
            RpcRequest.of(Base64.decodeBase64(request.getData().getPassword())));

        // 验证用户信息
        SysUser user = validateUserForLogin(request.getData().getName(), pwdResult.getData());

        // 生成token
        RpcResult<String> tokenResult = tokenService.createToken(RpcRequest.of(user.getUserId()));

        // 缓存token
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setToken(tokenResult.getData());
        loginInfo.setUserId(user.getUserId());
        loginInfo.setUserName(user.getUserName());
        loginInfo.setLoginIp(request.getData().getClientIp());
        loginInfo.setLoginTime(DateUtil.format(new Date(), STANDARD_STR));
        loginInfo.setUserAgent(request.getData().getUserAgent());
        tokenService.saveToken(RpcRequest.of(loginInfo));

        // 把用户信息存入redis（或者更新）
        userService.touchUser(RpcRequest.of(user));

        // 写入cookie
        //response.addCookie(new Cookie(COOKIE_SESSION_KEY_PREFIX, token));

        return RpcResult.success(tokenResult.getData());
    }

    @Override
    public RpcResult<Void> logout(RpcRequest<SysUser> request) {

        SysUser sysUser = request.getData();

        if (null != sysUser) {
            tokenService.removeToken(RpcRequest.of(sysUser.getToken()));

            userService.clearUser(RpcRequest.of(sysUser.getUserId()));
        }

        return RpcResult.success(null);
    }

    private void validateUserForCreate(String name, String password) {
        AssertUtil.isTrue(StringUtils.isNotBlank(name) && name.length() <= MAX_NAME_LENGTH, USER_NAME_LENGTH_INVALID,
            "用户名长度非法");
    }

    private SysUser validateUserForLogin(String name, String password) {
        RpcResult<SysUser> userResult = userService.loadSysUserByName(RpcRequest.of(name));
        SysUser user = userResult.getData();
        AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "用户不存在");

        // TODO：临时逻辑：超级账号第一次登陆，没有密码，用第一次登陆的密码初始化
        if (StringUtils.equals(user.getUserName(), "admin") && StringUtils.isBlank(user.getPassword())) {
            //String passwordEncrypted = bCryptPasswordEncoder.encode(password);
            //user.setPassword(passwordEncrypted);
            userService.updateSysUser(RpcRequest.of(user));
            return user;
        }

        boolean matches = bCryptPasswordEncoder.matches(password, user.getPassword());
        AssertUtil.isTrue(matches, INVALID_PARAM_EXCEPTION, "密码不正确");

        return user;
    }
}
