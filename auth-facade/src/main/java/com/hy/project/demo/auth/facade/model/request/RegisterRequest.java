package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.BaseRequest;

import java.util.List;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class RegisterRequest extends BaseRequest {
    private static final long serialVersionUID = -8847725781648580792L;

    private String name;
    private String password;

    private List<String> roleIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
