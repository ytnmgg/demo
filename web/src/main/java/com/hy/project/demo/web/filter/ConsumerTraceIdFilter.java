package com.hy.project.demo.web.filter;

import java.util.UUID;

import com.hy.project.demo.common.model.BaseRequest;
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
public class ConsumerTraceIdFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerTraceIdFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // before filter ...
        String traceId = MDC.get("traceId");

        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            LOGGER.info("create new traceId: {}", traceId);
        }

        Object[] args = invocation.getArguments();
        if (null != args) {
            for (Object arg : args) {
                if (arg instanceof BaseRequest) {
                    BaseRequest baseRequest = (BaseRequest)arg;
                    baseRequest.setTraceId(traceId);
                }
            }
        }

        Result result = invoker.invoke(invocation);

        // after filter ...
        return result;
    }


}
