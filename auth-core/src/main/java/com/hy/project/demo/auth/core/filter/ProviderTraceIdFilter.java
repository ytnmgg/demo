package com.hy.project.demo.auth.core.filter;

import java.util.UUID;

import com.hy.project.demo.common.model.BaseRequest;
import com.hy.project.demo.common.model.BaseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class ProviderTraceIdFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderTraceIdFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // before filter ...
        String traceId = null;
        Object[] args = invocation.getArguments();
        if (null != args) {
            for (Object arg : args) {
                if (arg instanceof BaseRequest) {
                    BaseRequest baseRequest = (BaseRequest)arg;
                    if (StringUtils.isNotBlank(baseRequest.getTraceId())) {
                        traceId = baseRequest.getTraceId();
                        MDC.put("traceId", traceId);
                        LOGGER.info("get traceId from base request: {}", traceId);
                        break;
                    }
                }
            }
        }

        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            LOGGER.info("create new traceId: {}", traceId);
        }

        Result result = invoker.invoke(invocation);

        if (null != result && null != result.getValue() && result.getValue() instanceof BaseResult) {
            BaseResult baseResult = (BaseResult)result.getValue();
            baseResult.setTraceId(traceId);
            LOGGER.info("put traceId to result: {}", traceId);
        }

        // after filter ...
        return result;
    }
}
