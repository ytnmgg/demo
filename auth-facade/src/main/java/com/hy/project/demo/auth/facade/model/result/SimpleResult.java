package com.hy.project.demo.auth.facade.model.result;

import com.hy.project.demo.common.model.BaseResult;

/**
 * @author rick.wl
 * @date 2023/04/03
 */
public class SimpleResult<T> extends BaseResult {
    private static final long serialVersionUID = 6984919196659857171L;

    private T data;

    public static <T> SimpleResult<T> of(T data) {
        SimpleResult<T> simpleResult = new SimpleResult<>();
        simpleResult.setData(data);
        return simpleResult;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
