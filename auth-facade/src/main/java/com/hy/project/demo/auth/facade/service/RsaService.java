package com.hy.project.demo.auth.facade.service;

import java.security.Key;

import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;

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
    RpcResult<String> getRsaPublicKeyString(RpcRequest<Void> request);

    RpcResult<Key> getRsaPublicKey(RpcRequest<Void> request);

    RpcResult<Key> getRsaPrivateKey(RpcRequest<Void> request);

    RpcResult<String> decryptByPrivateKey(RpcRequest<byte[]> request);
}
