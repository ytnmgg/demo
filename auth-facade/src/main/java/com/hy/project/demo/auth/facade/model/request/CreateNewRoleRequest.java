package com.hy.project.demo.auth.facade.model.request;

import java.util.List;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class CreateNewRoleRequest extends BaseRequest {
    private static final long serialVersionUID = -2030302124599669815L;

    private String name;
    private String code;
    private List<String> permissions;

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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
