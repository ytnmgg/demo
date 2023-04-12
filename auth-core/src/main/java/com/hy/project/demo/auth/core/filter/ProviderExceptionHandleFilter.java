package com.hy.project.demo.auth.core.filter;

import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.common.exception.DemoException;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.UNKNOWN_EXCEPTION;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class ProviderExceptionHandleFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderExceptionHandleFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);

            // 看了源码，有异常应该是到这里
            if (result.hasException()) {
                Throwable exception = result.getException();
                LOGGER.error(String.format("[PROVIDER-EXCEPTION][%s]", invocation.getMethodName()), exception);
                result.setValue(buildExceptionResult(exception));
                result.setException(null);
            }

            return result;

        } catch (Throwable e) {
            // 看了源码，应该不会抛异常到这里来，加上也是为了保险
            LOGGER.error(String.format("[PROVIDER-EXCEPTION][%s]", invocation.getMethodName()), e);
            return AsyncRpcResult.newDefaultAsyncResult(buildExceptionResult(e), invocation);
        }
    }

    private RpcResult<?> buildExceptionResult(Throwable e) {
        if (e instanceof DemoException) {
            DemoException demoException = (DemoException)e;
            return RpcResult.exception(demoException.getCode().getCode(),
                demoException.getMessage());
        } else {
            return RpcResult.exception(UNKNOWN_EXCEPTION.getCode(),
                UNKNOWN_EXCEPTION.getDescription());
        }
    }
}
