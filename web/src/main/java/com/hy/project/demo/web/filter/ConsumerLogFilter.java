package com.hy.project.demo.web.filter;

import com.hy.project.demo.common.model.BaseRequest;
import com.hy.project.demo.common.model.BaseResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class ConsumerLogFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // before filter ...
        Object[] args = invocation.getArguments();
        if (null != args) {
            for (Object arg : args) {
                if (arg instanceof BaseRequest) {
                    BaseRequest baseRequest = (BaseRequest)arg;
                    LOGGER.info("[CONSUMER-REQUEST][{}][{}]", invocation.getMethodName(), baseRequest);
                }
            }
        }

        Result result = invoker.invoke(invocation);

        // after filter ...
        if (null != result && null != result.getValue() && result.getValue() instanceof BaseResult) {
            BaseResult baseResult = (BaseResult)result.getValue();
            LOGGER.info("[CONSUMER-RESULT][{}][{}]", invocation.getMethodName(), baseResult);
        }

        return result;
    }
}
