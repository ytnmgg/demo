package com.hy.project.demo;

import java.util.Map;

import com.hy.project.demo.util.RsaUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
public class RsaUtilTest {

    @Test
    public void test() throws Throwable {

        //初始化密钥
        //生成密钥
        Map<String, Object> keyMap = RsaUtil.initKey();
        //公钥
        byte[] publicKey = RsaUtil.parsePublicKeyBytes(keyMap);

        //私钥
        byte[] privateKey = RsaUtil.parsePrivateKeyBytes(keyMap);
        System.out.println("公钥：" + Base64.encodeBase64String(publicKey));
        System.out.println("私钥：" + Base64.encodeBase64String(privateKey));

        System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
        String str = "RSA密码交换算法";
        System.out.println("===========甲方向乙方发送加密数据==============");
        System.out.println("原文：" + str);
        //甲方进行数据的加密
        byte[] code1 = RsaUtil.encryptByPrivateKey(str.getBytes(), privateKey);
        System.out.println("加密后的数据：" + Base64.encodeBase64String(code1));
        System.out.println("===========乙方使用甲方提供的公钥对数据进行解密==============");
        //乙方进行数据的解密
        byte[] decode1 = RsaUtil.decryptByPublicKey(code1, publicKey);
        System.out.println("乙方解密后的数据：" + new String(decode1));

        System.out.println("===========反向进行操作，乙方向甲方发送数据==============");

        str = "乙方向甲方发送数据RSA算法";

        System.out.println("原文:" + str);

        //乙方使用公钥对数据进行加密
        byte[] code2 = RsaUtil.encryptByPublicKey(str.getBytes(), publicKey);
        System.out.println("===========乙方使用公钥对数据进行加密==============");
        System.out.println("加密后的数据：" + Base64.encodeBase64String(code2));

        System.out.println("=============乙方将数据传送给甲方======================");
        System.out.println("===========甲方使用私钥对数据进行解密==============");

        //甲方使用私钥对数据进行解密
        byte[] decode2 = RsaUtil.decryptByPrivateKey(code2, privateKey);

        // 分段解密
        String s = RsaUtil.decryptByPrivateKeyWithSeg(code2, privateKey);

        System.out.println("甲方解密后的数据：" + new String(decode2));
        System.out.println("甲方解密后的数据（分段解密）：" + s);


    }
}
