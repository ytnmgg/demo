package com.hy.project.demo.common.model;

import com.hy.project.demo.common.exception.DemoExceptionEnum;
import org.apache.commons.lang3.StringUtils;

import static com.hy.project.demo.common.util.ResultUtil.CODE_200;

/**
 * @author rick.wl
 * @date 2023/04/03
 */
public class RpcResult<T> extends BaseResult {
    private static final long serialVersionUID = 6984919196659857171L;

    private T data;

    public static <T> RpcResult<T> success(T data) {
        RpcResult<T> result = new RpcResult<>();
        result.setCode(CODE_200);
        result.setData(data);
        return result;
    }

    public static <T> RpcResult<T> exception(DemoExceptionEnum exceptionEnum) {
        return exception(exceptionEnum.getCode(), exceptionEnum.getDescription());
    }

    public static <T> RpcResult<T> exception(String code, String message) {
        RpcResult<T> result = new RpcResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public boolean success() {
        return StringUtils.equals(getCode(), CODE_200);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
