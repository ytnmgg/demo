package com.hy.project.demo.auth.facade.service;

import java.security.Key;

import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
public interface RsaService {
    /**
     * 获取RSA公钥，每次开机时产生，缓存在JVM中
     *
     * @return 结果
     * @throws Throwable 异常
     */
    SimpleResult<String> getRsaPublicKeyString(BaseRequest request);

    SimpleResult<Key> getRsaPublicKey(BaseRequest request);

    SimpleResult<Key> getRsaPrivateKey(BaseRequest request);

    SimpleResult<String> decryptByPrivateKey(SimpleRequest<byte[]> request);
}
