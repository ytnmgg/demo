package com.hy.project.demo.service.sso.impl;

import java.security.Key;
import java.util.Map;

import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.util.RsaUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
@Service
public class RsaServiceImpl implements RsaService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);

    private Map<String, Object> keys;

    @Override
    public String getRsaPublicKeyString() {
        byte[] publicKey = RsaUtil.parsePublicKeyBytes(keys);
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
        Map<String, Object> keyMap = RsaUtil.initKey();
        this.setKeys(keyMap);
        LOGGER.info(String.format("RSA PUBLIC KEY: %s", this.getRsaPublicKeyString()));
    }

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }
}
