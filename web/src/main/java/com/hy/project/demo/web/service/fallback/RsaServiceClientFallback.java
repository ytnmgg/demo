package com.hy.project.demo.web.service.fallback;

import com.hy.project.demo.common.model.AjaxResult;
import com.hy.project.demo.web.service.FeignRsaServiceClient;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2023/03/29
 */
@Component
public class RsaServiceClientFallback implements FeignRsaServiceClient {
    @Override
    public AjaxResult getRsaPubKey() {
        return AjaxResult.fail("REMOTE_CALL_FAIL", "in fallback...");
    }
}
