package com.hy.project.demo.web.service;

import com.hy.project.demo.common.model.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author rick.wl
 * @date 2023/03/29
 */
//@FeignClient(name = "demo-auth-core", configuration = FeignConfiguration.class,
//    fallback = RsaServiceClientFallback.class)
public interface FeignRsaServiceClient {

    @GetMapping("/rsa/get_rsa_pub_key.json")
    AjaxResult getRsaPubKey();

}
