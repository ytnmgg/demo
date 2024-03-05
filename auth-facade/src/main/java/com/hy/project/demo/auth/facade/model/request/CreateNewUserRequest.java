package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.BaseRequest;

import java.util.List;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class CreateNewUserRequest extends BaseRequest {
    private static final long serialVersionUID = 1016909938987268727L;

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
