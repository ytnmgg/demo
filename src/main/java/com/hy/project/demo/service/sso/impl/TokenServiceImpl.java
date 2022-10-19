package com.hy.project.demo.service.sso.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.hy.project.demo.exception.DemoExceptionEnum;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.service.sso.TokenService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.EnvUtil;
import com.hy.project.demo.util.SsoUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.util.SsoUtil.createRedisTokenKey;
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
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String DEFAULT_REDIS_TOKEN_TIMEOUT_MINUTES = "30";

    @Value("${sso.token.header}")
    private String tokenHeader;

    @Value("${sso.token.expireTime}")
    private int tokenExpireTime;

    @Autowired
    RsaService rsaService;

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public SysUser getUserByToken(HttpServletRequest request) {

        // 获取请求携带的令牌
        String token = getTokenFromHeader(request, tokenHeader);

        if (StringUtils.isBlank(token)) {
            return null;
        }

        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, StringUtils.EMPTY);
        }

        // 验证token是否在缓存中
        if (!checkTokenInCache(createRedisTokenKey(token))) {
            // 伪造的token或者token过期
            return null;
        }

        try {
            Claims claims = parseToken(token, rsaService.getRsaPublicKey());
            String userId = getUid(claims);

            // 刷新token缓存时间
            touchToken(token);

            // 通过userId去缓存拿用户并刷新缓存时间（没有就放进去）
            return userService.touchUser(userId);

        } catch (Exception e) {
            LOGGER.error("parse user info failed", e);
        }

        return null;
    }

    @Override
    public String createToken(String userId) {
        return SsoUtil.createToken(UUID.randomUUID().toString(), userId, rsaService.getRsaPrivateKey());
    }

    @Override
    public void cacheToken(String token) {
        String key = createRedisTokenKey(token);
        boolean result = redisService.set(key, "", getTokenExpireTime(), TimeUnit.MINUTES);
        AssertUtil.isTrue(result, DemoExceptionEnum.REDIS_EXCEPTION, "cache token error");
    }

    private boolean checkTokenInCache(String tokenKey) {
        return redisService.exists(tokenKey);
    }

    private Long getTokenExpireTime() {
        String expireTime = EnvUtil.getEnvValue("sso.token.expireTime");
        if (StringUtils.isBlank(expireTime)) {
            LOGGER.error("failed to get config: sso.token.expireTime, use default value");
            expireTime = DEFAULT_REDIS_TOKEN_TIMEOUT_MINUTES;
        }
        return Long.parseLong(expireTime);
    }

    private void touchToken(String token) {
        redisService.expire(createRedisTokenKey(token), getTokenExpireTime(), TimeUnit.MINUTES);
    }
}
