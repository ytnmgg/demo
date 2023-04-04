package com.hy.project.demo.auth.facade.model.request;

import java.util.List;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class UpdateRolePermissionRequest extends BaseRequest {
    private static final long serialVersionUID = -3752904355769581096L;

    private String roleId;
    private List<String> permissions;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
