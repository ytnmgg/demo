package com.hy.project.demo.auth.facade.model.request;

import java.util.List;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class UpdateUserRoleRequest extends BaseRequest {
    private static final long serialVersionUID = -3713130381743543401L;

    private String userId;
    private List<String> roleIds;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
