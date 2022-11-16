package com.hy.project.demo.controller.request;

import java.util.List;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public class RoleCreateRequest {
    private String roleName;
    private String roleKey;
    private List<String> permissionIds;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
