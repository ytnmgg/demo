package com.hy.project.demo.auth.facade.model;

import com.hy.project.demo.common.model.BaseResult;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class RsaGetResult<T> extends BaseResult {

    private static final long serialVersionUID = -8255451172342712673L;

    private T data;

    public static <T> RsaGetResult<T> of(T data) {
        RsaGetResult<T> result = new RsaGetResult<>();
        result.setData(data);
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
