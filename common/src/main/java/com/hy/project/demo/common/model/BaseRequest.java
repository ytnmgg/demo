package com.hy.project.demo.common.model;

/**
 * @author rick.wl
 * @date 2022/09/13
 */
public class BaseRequest extends ToString {
    private static final long serialVersionUID = 1713550624074540476L;

    private String traceId;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
