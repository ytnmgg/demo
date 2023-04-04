package com.hy.project.demo.web.model;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author rick.wl
 * @date 2022/12/01
 */
public class RequestContext {
    private HttpServletRequest request;
    private String traceId;
    private Long enterTime;
    private List<Object> requestArgs;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Long enterTime) {
        this.enterTime = enterTime;
    }

    public List<Object> getRequestArgs() {
        return requestArgs;
    }

    public void setRequestArgs(List<Object> requestArgs) {
        this.requestArgs = requestArgs;
    }
}
