package com.hy.project.demo.web.model;

/**
 * @author rick.wl
 * @date 2022/12/01
 */
public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT_THREAD_LOCAL = ThreadLocal.withInitial(
        () -> new RequestContext());

    public static String getRequestId() {
        RequestContext requestContext = getCurrentRequestContext();

        //if (TextUtils.isEmpty(requestContext.getRequestId())) {
        //    String requestId = UUID.randomUUID().toString();
        //    requestContext.setRequestId(requestId);
        //}
        return requestContext.getRequestId();
    }

    public static RequestContext getCurrentRequestContext() {
        return REQUEST_CONTEXT_THREAD_LOCAL.get();
    }

    public static void setCurrentRequestContext(RequestContext context) {
        REQUEST_CONTEXT_THREAD_LOCAL.set(context);
    }
}
