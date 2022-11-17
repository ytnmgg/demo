package com.hy.project.demo.service.auth.impl;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.hy.project.demo.service.auth.RsaService;
import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.util.RsaUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.constant.RedisConstants.KEY_KEYS;
import static com.hy.project.demo.util.RsaUtil.PRIVATE_KEY;
import static com.hy.project.demo.util.RsaUtil.PUBLIC_KEY;
import static com.hy.project.demo.util.RsaUtil.decodePrivateKeyFromBase64;
import static com.hy.project.demo.util.RsaUtil.decodePublicKeyFromBase64;
import static com.hy.project.demo.util.RsaUtil.encodeKeyToBase64;
import static com.hy.project.demo.util.RsaUtil.parsePrivateKeyBytes;
import static com.hy.project.demo.util.RsaUtil.parsePublicKeyBytes;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
@Service
public class RsaServiceImpl implements RsaService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);

    private Map<String, Object> keys;

    @Autowired
    RedisService redisService;

    @Override
    public String getRsaPublicKeyString() {
        byte[] publicKey = parsePublicKeyBytes(keys);
        return new String(Base64.encodeBase64(publicKey));
    }

    @Override
    public Key getRsaPublicKey() {
        return RsaUtil.parsePublicKey(keys);
    }

    @Override
    public String getRsaPrivateKeyString() {
        byte[] privateKey = RsaUtil.parsePrivateKeyBytes(keys);
        return new String(Base64.encodeBase64(privateKey));
    }

    @Override
    public Key getRsaPrivateKey() {
        return RsaUtil.parsePrivateKey(keys);
    }

    @Override
    public String decryptByPrivateKey(byte[] data) {
        byte[] privateKey = RsaUtil.parsePrivateKeyBytes(keys);
        return RsaUtil.decryptByPrivateKeyWithSeg(data, privateKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initKeys();
    }

    private void initKeys() throws Exception {
        Map<String, Object> keys = restoreKeysFromRedis();
        if (null != keys) {
            // 缓存到jvm中
            this.setKeys(keys);

        } else {
            // redis中没有找到keys，初始化一个，并缓存起来 TODO：多服务以后的一致性问题
            keys = RsaUtil.initKey();
            // 缓存到jvm中
            this.setKeys(keys);
            // 缓存到redis中
            cacheKeysToRedis(keys);
        }
    }

    private Map<String, Object> restoreKeysFromRedis() {
        try {
            Object keys = redisService.get(KEY_KEYS);
            if (null == keys) {
                return null;
            }

            Map<String, String> keyStrings = JSON.parseObject((String)keys,
                new TypeReference<Map<String, String>>() {});

            Map<String, Object> result = new HashMap<>();
            result.put(PUBLIC_KEY, decodePublicKeyFromBase64(keyStrings.get(PUBLIC_KEY)));
            result.put(PRIVATE_KEY, decodePrivateKeyFromBase64(keyStrings.get(PRIVATE_KEY)));
            return result;
        } catch (Exception e) {
            LOGGER.error("failed to restore keys from redis", e);
            return null;
        }
    }

    private void cacheKeysToRedis(Map<String, Object> keys) {
        Map<String, String> newKeysToRedis = new HashMap<>();
        newKeysToRedis.put(PUBLIC_KEY, encodeKeyToBase64(parsePublicKeyBytes(keys)));
        newKeysToRedis.put(PRIVATE_KEY, encodeKeyToBase64(parsePrivateKeyBytes(keys)));
        redisService.set(KEY_KEYS, JSON.toJSONString(newKeysToRedis));
    }

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }
}