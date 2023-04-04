package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/03
 */
public class SimpleRequest<T> extends BaseRequest {
    private static final long serialVersionUID = 597333839220424227L;

    private T data;

    public static <T> SimpleRequest<T> of(T data) {
        SimpleRequest<T> simpleRequest = new SimpleRequest<>();
        simpleRequest.setData(data);
        return simpleRequest;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
