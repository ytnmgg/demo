package com.hy.project.demo.web.model;

import java.util.List;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public class UserRoleUpdateRequest {
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
