package com.hy.project.demo.auth.core.service.impl;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.auth.core.util.SsoUtil;
import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.model.BaseRequest;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.service.redis.RedisService;
import com.hy.project.demo.common.util.EnvUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.auth.core.util.SsoUtil.getUid;
import static com.hy.project.demo.auth.core.util.SsoUtil.parseToken;
import static com.hy.project.demo.common.constant.RedisConstants.KEY_LOGIN_HASH;
import static com.hy.project.demo.common.constant.RedisConstants.KEY_LOGIN_SET;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
@Service
@DubboService
public class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String DEFAULT_REDIS_TOKEN_TIMEOUT_MINUTES = "30";
    private static final String DEFAULT_REDIS_MAX_EXPIRE_TOKEN_COUNT = "100";

    @Value("${sso.token.expireTime}")
    private int tokenExpireTime;

    @Autowired
    RsaService rsaService;

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public SimpleResult<SysUser> getUserByToken(SimpleRequest<String> request) {

        String token = request.getData();
        if (StringUtils.isBlank(token)) {
            return SimpleResult.of(null);
        }

        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, StringUtils.EMPTY);
        }

        // 验证token是否在缓存中
        if (!checkTokenInCache(token)) {
            // 伪造的token或者token过期
            return SimpleResult.of(null);
        }

        try {
            SimpleResult<Key> pubKeyResult = rsaService.getRsaPublicKey(new BaseRequest());
            Claims claims = parseToken(token, pubKeyResult.getData());
            String userId = getUid(claims);

            // 刷新token缓存时间
            touchToken(SimpleRequest.of(token));

            // 通过userId去缓存拿用户并刷新缓存时间（没有就放进去）
            SimpleResult<SysUser> userResult = userService.touchUserById(SimpleRequest.of(userId));
            SysUser user = userResult.getData();
            user.setToken(token);

            return SimpleResult.of(user);

        } catch (Exception e) {
            LOGGER.error("parse user info failed", e);
        }

        return SimpleResult.of(null);
    }

    @Override
    public SimpleResult<String> createToken(SimpleRequest<String> request) {
        SimpleResult<Key> result = rsaService.getRsaPrivateKey(new BaseRequest());
        return SimpleResult.of(SsoUtil.createToken(UUID.randomUUID().toString(), request.getData(), result.getData()));
    }

    @Override
    public BaseResult removeToken(SimpleRequest<String> request) {
        redisService.removeHash(KEY_LOGIN_HASH, request.getData());
        redisService.removeZSet(KEY_LOGIN_SET, request.getData());
        return new BaseResult();
    }

    @Override
    public BaseResult saveToken(SimpleRequest<LoginInfo> request) {
        LoginInfo loginInfo = request.getData();
        redisService.setHash(KEY_LOGIN_HASH, loginInfo.getToken(), JSON.toJSONString(loginInfo));
        redisService.addToZSet(KEY_LOGIN_SET, loginInfo.getToken(), System.currentTimeMillis());
        return new BaseResult();
    }

    @Override
    public BaseResult expireTokens(BaseRequest request) {
        // 捞取的token最大score（登录时间毫秒数），小于这些登录时间的，都会被捞起来踢掉
        long maxScore = System.currentTimeMillis() - getTokenExpireTimeMillis();
        // 定时任务每次捞取的token最大数量
        long maxTokenCount = getMaxExpireTokenCount();

        Set<String> tokens = redisService.rangeZSetByScore(KEY_LOGIN_SET, 0, maxScore, 0, maxTokenCount);
        LOGGER.info("{} tokens will be expired", tokens.size());

        if (CollectionUtils.isNotEmpty(tokens)) {

            String[] tokenArray = new String[tokens.size()];
            tokens.toArray(tokenArray);

            redisService.removeHash(KEY_LOGIN_HASH, tokenArray);
            redisService.removeZSet(KEY_LOGIN_SET, tokenArray);

            LOGGER.info("{} tokens expired", tokens.size());
        }
        return new BaseResult();
    }

    @Override
    public BaseResult touchToken(SimpleRequest<String> request) {
        redisService.incrementZSetScore(KEY_LOGIN_SET, request.getData(), getTokenExpireTimeMillis());
        return new BaseResult();
    }

    @Override
    public PageResult<List<LoginInfo>> pageQueryLoginInfo(PageRequest request) {
        PageResult<List<LoginInfo>> result = new PageResult<>();
        result.setPageIndex(request.getPageIndex());
        result.setPageSize(request.getPageSize());

        Long size = redisService.sizeZSet(KEY_LOGIN_SET);

        if (null == size || size == 0L) {
            result.setTotalCount(0L);
            return result;
        }

        result.setTotalCount(size);

        long start = request.getPageSize() * (request.getPageIndex() - 1L);
        long end = request.getPageSize() * request.getPageIndex() - 1L;
        Set<String> tokens = redisService.reverseRangeZSet(KEY_LOGIN_SET, start, end);

        if (CollectionUtils.isNotEmpty(tokens)) {
            List<Object> logins = redisService.multiGetHash(KEY_LOGIN_HASH, tokens);

            if (CollectionUtils.isNotEmpty(logins)) {
                List<LoginInfo> loginInfoList = new ArrayList<>();
                logins.forEach(str -> {
                    LoginInfo loginInfo = JSON.parseObject((String)str, LoginInfo.class);
                    String token = loginInfo.getToken();
                    loginInfo.setToken(String.format("***%s", StringUtils.substring(token, 88)));
                    loginInfoList.add(loginInfo);
                });
                result.setData(loginInfoList);
            }
        }

        return result;
    }

    private boolean checkTokenInCache(String token) {
        Long rank = redisService.rankZSet(KEY_LOGIN_SET, token);
        return rank != null && rank >= 0L;
    }

    private Long getTokenExpireTimeMillis() {
        String expireTime = EnvUtil.getEnvValue("sso.token.expireTime");
        if (StringUtils.isBlank(expireTime)) {
            LOGGER.error("failed to get config: sso.token.expireTime, use default value");
            expireTime = DEFAULT_REDIS_TOKEN_TIMEOUT_MINUTES;
        }
        return Long.parseLong(expireTime) * 60 * 1000;
    }

    private Long getMaxExpireTokenCount() {
        String maxCount = EnvUtil.getEnvValue("sso.token.maxExpireCount");
        if (StringUtils.isBlank(maxCount)) {
            LOGGER.error("failed to get config: sso.token.maxExpireCount, use default value");
            maxCount = DEFAULT_REDIS_MAX_EXPIRE_TOKEN_COUNT;
        }
        return Long.parseLong(maxCount);
    }

}
