package com.hy.project.demo.service.sso.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.hy.project.demo.service.sso.RsaService;
import com.hy.project.demo.util.RsaUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
@Service
public class RsaServiceImpl implements RsaService, InitializingBean {
    private Map<String, Object> keys;

    @Override
    public String getRsaPublicKey() throws Throwable {
        byte[] publicKey = RsaUtil.getPublicKey(keys);
        return new String(Base64.encodeBase64(publicKey));
    }

    @Override
    public String decryptByPrivateKey(byte[] data) throws Throwable {
        byte[] privateKey = RsaUtil.getPrivateKey(keys);
        return RsaUtil.decryptByPrivateKeyWithSeg(data, privateKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> keyMap = RsaUtil.initKey();
        this.setKeys(keyMap);
    }

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }
}
