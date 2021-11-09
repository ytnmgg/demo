package com.hy.project.demo.model;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
public class DemoResult<T> extends ToString{
    private static final long serialVersionUID = 8597724872597824768L;

    private boolean success = true;
    private String code;
    private String message;
    private T data;

    public static<S> DemoResult<S> buildSuccessResult(S data) {
        DemoResult<S> result = new DemoResult<>();
        result.setData(data);
        return result;
    }

    public static<S> DemoResult<S> buildErrorResult(S data, String code, String message) {
        DemoResult<S> result = new DemoResult<>();
        result.setSuccess(false);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
