package com.hy.project.demo.common.model;

import com.hy.project.demo.common.exception.DemoExceptionEnum;
import com.hy.project.demo.common.util.ResultUtil;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public class AjaxResult extends BaseResult {
    private static final long serialVersionUID = -3309273091328758404L;

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static AjaxResult success(Object data) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(ResultUtil.CODE_200);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult fail(String code, String message) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(code);
        ajaxResult.setMessage(message);
        return ajaxResult;
    }

    public static AjaxResult fail(DemoExceptionEnum exceptionEnum) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(exceptionEnum.getCode());
        ajaxResult.setMessage(exceptionEnum.getDescription());
        return ajaxResult;
    }
}
