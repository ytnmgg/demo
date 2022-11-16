package com.hy.project.demo.controller.request;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
public class PermissionCreateRequest {
    private String permissionName;
    private String permissionKey;

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }
}
