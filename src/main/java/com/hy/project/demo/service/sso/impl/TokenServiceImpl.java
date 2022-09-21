package com.hy.project.demo.service.sso.impl;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.security.LoginUser;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.service.sso.TokenService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.SsoUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.util.SsoUtil.getTokenFromHeader;
import static com.hy.project.demo.util.SsoUtil.getUid;
import static com.hy.project.demo.util.SsoUtil.parseToken;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
@Service
public class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Value("${sso.token.header}")
    private String tokenHeader;

    @Value("${sso.token.expireTime}")
    private int tokenExpireTime;

    @Autowired
    RsaService rsaService;

    @Autowired
    UserService userService;

    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {

        // 获取请求携带的令牌
        String token = getTokenFromHeader(request, tokenHeader);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token, rsaService.getRsaPublicKey());

                // 通过token去缓存拿用户，TODO，改成通过uid去拿
                //String userKey = getTokenKey(uuid);
                //LoginUser user = redisCache.getCacheObject(userKey);

                // mock
                SysUser user = userService.loadSysUserByUserId(getUid(claims));
                LoginUser loginUser = new LoginUser();
                loginUser.setUserId(user.getUserId());
                loginUser.setToken(token);
                //loginUser.setExpireTime();
                //loginUser.setPermissions();
                loginUser.setUser(user);
                return loginUser;

            } catch (Exception e) {
                LOGGER.error("parse user info failed", e);
            }
        }
        return null;
    }

    @Override
    public String createToken(String userId) {
        return SsoUtil.createToken(UUID.randomUUID().toString(), userId, rsaService.getRsaPrivateKey());
    }
}
