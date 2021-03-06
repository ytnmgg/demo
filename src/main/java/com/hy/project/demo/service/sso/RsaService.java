package com.hy.project.demo.service.sso;

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
    String getRsaPublicKey() throws Throwable;

    /**
     * 用JVM保存的私钥去解密信息
     *
     * @param data 待解密数据
     * @return 结果
     * @throws Throwable 异常
     */
    String decryptByPrivateKey(byte[] data) throws Throwable;
}
