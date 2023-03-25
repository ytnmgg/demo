package com.hy.project.demo.web.model;

import java.util.List;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public class RolePermissionUpdateRequest {
    private String roleId;
    private List<String> permissionIds;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
