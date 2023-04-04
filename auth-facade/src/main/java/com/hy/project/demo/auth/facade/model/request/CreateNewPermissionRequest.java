package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class CreateNewPermissionRequest extends BaseRequest {
    private static final long serialVersionUID = -5840807541656477823L;

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
