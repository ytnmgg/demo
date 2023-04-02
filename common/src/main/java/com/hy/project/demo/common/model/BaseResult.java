package com.hy.project.demo.common.model;

/**
 * @author rick.wl
 * @date 2022/09/13
 */
public class BaseResult extends ToString {
    private static final long serialVersionUID = 3049615573477024993L;

    private String code;
    private String message;
    private String traceId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
