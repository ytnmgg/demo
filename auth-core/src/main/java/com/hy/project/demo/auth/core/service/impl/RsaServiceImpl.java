package com.hy.project.demo.auth.core.service.impl;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.hy.project.demo.auth.core.util.RsaUtil;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.common.service.redis.RedisService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.auth.core.util.RsaUtil.PRIVATE_KEY;
import static com.hy.project.demo.auth.core.util.RsaUtil.PUBLIC_KEY;
import static com.hy.project.demo.auth.core.util.RsaUtil.decodePrivateKeyFromBase64;
import static com.hy.project.demo.auth.core.util.RsaUtil.decodePublicKeyFromBase64;
import static com.hy.project.demo.auth.core.util.RsaUtil.encodeKeyToBase64;
import static com.hy.project.demo.auth.core.util.RsaUtil.parsePrivateKeyBytes;
import static com.hy.project.demo.auth.core.util.RsaUtil.parsePublicKeyBytes;
import static com.hy.project.demo.common.constant.RedisConstants.KEY_KEYS;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
@Service
@DubboService
public class RsaServiceImpl implements RsaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);

    private Map<String, Object> keys;

    @Autowired
    RedisService redisService;

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Override
    public RpcResult<String> getRsaPublicKeyString(RpcRequest<Void> request) {
        byte[] publicKey = parsePublicKeyBytes(keys);
        return RpcResult.success(new String(Base64.encodeBase64(publicKey)));
    }

    @Override
    public RpcResult<Key> getRsaPublicKey(RpcRequest<Void> request) {
        return RpcResult.success(RsaUtil.parsePublicKey(keys));
    }

    @Override
    public RpcResult<Key> getRsaPrivateKey(RpcRequest<Void> request) {
        return RpcResult.success(RsaUtil.parsePrivateKey(keys));
    }

    @Override
    public RpcResult<String> decryptByPrivateKey(RpcRequest<byte[]> request) {
        byte[] privateKey = parsePrivateKeyBytes(keys);
        return RpcResult.success(RsaUtil.decryptByPrivateKeyWithSeg(request.getData(), privateKey));
    }

    @PostConstruct
    public void init() throws Exception {
        initKeys();

        //ConfigService configService = nacosConfigManager.getConfigService();
        //String result = configService.getConfig("SOME_THING_NEED_TO_CONFIG", "DEFAULT_GROUP", 500);
        //LOGGER.info("nacos config is: {}", result);
        //
        //configService.addListener("SOME_THING_NEED_TO_CONFIG", "DEFAULT_GROUP", new Listener() {
        //    @Override
        //    public Executor getExecutor() {
        //        return Executors.newSingleThreadExecutor();
        //    }
        //
        //    @Override
        //    public void receiveConfigInfo(String configInfo) {
        //        LOGGER.info("received nacos config is: {}", configInfo);
        //    }
        //});
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
